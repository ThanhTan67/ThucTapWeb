package controller.client.auth;

import model.*;
import dao.client.AccountDAO;
import dao.client.LOG_LEVEL;
import dao.client.Logging;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.util.Map;

import org.json.JSONObject;

@WebServlet(name = "LoginControll", value = "/LoginControll")
public class LoginControll extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("identifier")) {
                    request.setAttribute("identifier", URLDecoder.decode(cookie.getValue(), "UTF-8"));
                }
                if (cookie.getName().equals("passW")) {
                    request.setAttribute("password", URLDecoder.decode(cookie.getValue(), "UTF-8"));
                }
            }
        }
        request.getRequestDispatcher("/WEB-INF/client/login.jsp").forward(request, response);
    }

    private String readResponseBody(HttpURLConnection connection) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String identifier = request.getParameter("identifier");
        String passWord = request.getParameter("password");
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();

        // --- Gọi GeoIP API, nhưng có try/catch để không bung lỗi ---
        String countryName = "Unknown";
        try {
            URL url = new URL("https://geoip.svc.nvidia.com/json/" + ipAddress);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String responseBody = readResponseBody(connection);
                JSONObject jsonObject = new JSONObject(responseBody);
                countryName = jsonObject.optString("country_name", "Unknown");
                System.out.println("Quốc gia của bạn: " + countryName);
            } else {
                System.err.println("Lỗi khi kết nối với API GeoIP");
            }
        } catch (Exception e) {
            System.err.println("Không thể kết nối GeoIP: " + e.getMessage());
        }

        boolean checkSpaceIdentifier = (identifier == null || identifier.trim().isEmpty());
        boolean checkSpacePass = (passWord == null || passWord.trim().isEmpty());
        boolean checkEmailExist = false, checkPhoneExist = false;

        if (checkSpaceIdentifier) {
            request.setAttribute("errorIdenty", "Vui lòng nhập email hoặc số điện thoại");
        }
        if (checkSpacePass) {
            request.setAttribute("errorP", "Vui lòng nhập mật khẩu");
            request.setAttribute("identifier", identifier);
        }

        if (!checkSpaceIdentifier && !checkSpacePass) {
            String enpass = Encode.toSHA1(passWord);
            Account account = null;
            AccountDAO dao = new AccountDAO();

            if (isValidEmail(identifier)) {
                checkEmailExist = AccountDAO.checkFieldExists("email", identifier);
                if (checkEmailExist) {
                    if (AccountDAO.isAccountLocked("email", identifier)) {
                        request.setAttribute("error", "Tài khoản của bạn đã bị khóa do đăng nhập sai quá nhiều lần. Vui lòng thử lại sau 15 phút.");
                        request.setAttribute("identifier", identifier);
                        request.getRequestDispatcher("/WEB-INF/client/login.jsp").forward(request, response);
                        return;
                    }
                    account = dao.getAccountByField("email", identifier, enpass);
                }
            } else if (isValidPhone(identifier)) {
                checkPhoneExist = AccountDAO.checkFieldExists("phonenumber", identifier);
                if (checkPhoneExist) {
                    if (AccountDAO.isAccountLocked("phonenumber", identifier)) {
                        request.setAttribute("error", "Tài khoản của bạn đã bị khóa do đăng nhập sai quá nhiều lần. Vui lòng thử lại sau 15 phút.");
                        request.setAttribute("identifier", identifier);
                        request.getRequestDispatcher("/WEB-INF/client/login.jsp").forward(request, response);
                        return;
                    }
                    account = AccountDAO.getAccountByField("phonenumber", identifier, enpass);
                }
            }

            Log log = new Log();
            log.setSourceIP(ipAddress);
            log.setUserAgent(userAgent);
            log.setNational(countryName);
            log.setActionType("LOGIN");
            log.setModule("/controller/auth/LoginControll");

            if (account != null) {
                AccountDAO.resetFailedAttempts(account.getId());
                log.setLogLevel(LOG_LEVEL.INFO);
                log.setLogContent("Login success");
                log.setAccount(account);
                Logging.login(log);

                HttpSession session = request.getSession();
                session.setAttribute("account", account);
                session.setMaxInactiveInterval(60 * 60);

                Map<Integer, OrderDetail> cart = Cart.readCartFromCookies(request, account.getId());
                session.setAttribute("size", cart.size());

                Cookie c1 = new Cookie("identifier", URLEncoder.encode(identifier, "UTF-8"));
                Cookie c2 = new Cookie("passW", URLEncoder.encode(passWord, "UTF-8"));
                c1.setMaxAge(60 * 60 * 24 * 30);
                c2.setMaxAge(60 * 60 * 24 * 30);
                response.addCookie(c1);
                response.addCookie(c2);

                if (account.getRole().getId() == 0) {
                    response.sendRedirect(request.getContextPath() + "/IndexControll");
                } else if (account.getRole().getId() == 1) {
                    response.sendRedirect(request.getContextPath() + "/IndexAdminControll");
                }
            } else {
                String error = "Tài khoản hoặc mật khẩu không đúng.";
                request.setAttribute("error", error);
                request.setAttribute("identifier", identifier);
                request.getRequestDispatcher("/WEB-INF/client/login.jsp").forward(request, response);
            }
        } else {
            request.getRequestDispatcher("/WEB-INF/client/login.jsp").forward(request, response);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email != null && email.matches(emailRegex);
    }

    private boolean isValidPhone(String phone) {
        String phoneRegex = "^(\\+84|0)\\d{9,10}$";
        return phone != null && phone.matches(phoneRegex);
    }
}
