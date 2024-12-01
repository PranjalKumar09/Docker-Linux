package com.ecom.util;

import com.ecom.entity.ProductOrder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class CommonUtil {

    private static JavaMailSender mailSender;

    // Inject the JavaMailSender statically
    @Autowired
    public CommonUtil(JavaMailSender mailSender) {
        CommonUtil.mailSender = mailSender;
    }

    /**
     * Sends an email with the given URL and email address.
     *
     * @param url   The URL to include in the email.
     * @param email The recipient's email address.
     * @return true if the email is sent successfully, false otherwise.
     */
    public static boolean sendMail(String url, String email) {
        try {
            String subject = "Reset Password";

            // Email content
            String content = "Hello,\n\n" +
                    "We received a request to reset your password. " +
                    "Please click the link below to reset it:\n\n" +
                    url + "\n\n" +
                    "If you did not request a password reset, you can safely ignore this email.\n\n" +
                    "Best regards,\n" +
                    "Your Team";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom("coderkumarshukla@gmail.com", "Shopping Cart Kumar");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, false); // Use `false` for plain text

            mailSender.send(message);

            return true; // Email sent successfully
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace(); // Log the exception for debugging
            return false; // Return false if an exception occurs
        }
    }

    /**
     * Generates the URL from the HttpServletRequest.
     *
     * @param request The HttpServletRequest instance.
     * @return The generated URL.
     */
    public static String generateUrl(HttpServletRequest request) {
        return request.getRequestURL().toString();
    }


    public Boolean sendMailForProductOrder(ProductOrder productOrder) {
        try {
            String subject = "Your Order Status Update";

            // Enhanced HTML email content
            String content =
                    "<div style='font-family: Arial, sans-serif; font-size: 14px; color: #333;'>"
                            + "<div style='text-align: center; margin-bottom: 20px;'>"
                            + "<img src='https://via.placeholder.com/150' alt='Shopping Cart Logo' style='width: 150px;'>"
                            + "</div>"
                            + "<p>Hello <b>" + productOrder.getOrderAddress().getFirstName() + "</b>,</p>"
                            + "<p>Thank you for your order! Below are the details:</p>"
                            + "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse; width: 100%; margin-top: 20px;'>"
                            + "  <thead>"
                            + "    <tr style='background-color: #f2f2f2;'>"
                            + "      <th style='text-align: left;'>Product Name</th>"
                            + "      <th style='text-align: left;'>Category</th>"
                            + "      <th style='text-align: center;'>Quantity</th>"
                            + "      <th style='text-align: right;'>Price</th>"
                            + "      <th style='text-align: left;'>Payment Type</th>"
                            + "      <th style='text-align: left;'>Order Status</th>"
                            + "    </tr>"
                            + "  </thead>"
                            + "  <tbody>"
                            + "    <tr>"
                            + "      <td>" + productOrder.getProduct().getTitle() + "</td>"
                            + "      <td>" + productOrder.getProduct().getCategory() + "</td>"
                            + "      <td style='text-align: center;'>" + productOrder.getQuantity() + "</td>"
                            + "      <td style='text-align: right;'>$" + productOrder.getPrice() + "</td>"
                            + "      <td>" + productOrder.getPaymentType() + "</td>"
                            + "      <td>" + productOrder.getStatus() + "</td>"
                            + "    </tr>"
                            + "  </tbody>"
                            + "</table>"
                            + "<p style='margin-top: 20px;'>Track your order using the link below:</p>"
                            + "<p><a href='https://example.com/track-order' style='color: #1a73e8;'>Track My Order</a></p>"
                            + "<p>If you have any questions, contact us at <a href='mailto:support@example.com'>support@example.com</a>.</p>"
                            + "<p>Best regards,<br><b>Pranjal Kumar</b><br>Shopping Cart Team</p>"
                            + "<footer style='margin-top: 30px; text-align: center; font-size: 12px; color: #777;'>"
                            + "  &copy; 2024 Shopping Cart. All rights reserved."
                            + "</footer>"
                            + "</div>";

            // Prepare and send the email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // Use `true` for HTML content

            helper.setFrom("coderkumarshukla@gmail.com", "Shopping Cart Kumar");
            helper.setTo(productOrder.getOrderAddress().getEmail());
            helper.setSubject(subject);
            helper.setText(content, true); // Use `true` for HTML

            mailSender.send(message);

            return true; // Email sent successfully
        } catch (MessagingException | UnsupportedEncodingException e) {
            // Log the exception for debugging
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
            return false; // Return false if an exception occurs
        }
    }


}
