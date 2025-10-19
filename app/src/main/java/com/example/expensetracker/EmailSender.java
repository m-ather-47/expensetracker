package com.example.expensetracker;

import android.util.Log;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    private static final String TAG = "EmailSender";

    public static void sendEmail(final String fromEmail, final String password, final String toEmail, final String subject, final String body) throws MessagingException {
        Properties props = new Properties();
        // Gmail SMTP settings (STARTTLS)
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        // Ensure modern TLS and reasonable timeouts
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.connectiontimeout", "10000"); // 10s
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        // Route JavaMail debug output to Android logcat for easier diagnostics
        session.setDebug(true);
        session.setDebugOut(new PrintStream(new OutputStream() {
            private StringBuilder buf = new StringBuilder();
            @Override
            public void write(int b) {
                char c = (char) b;
                if (c == '\n' || c == '\r') {
                    if (buf.length() > 0) {
                        Log.d(TAG, buf.toString());
                        buf.setLength(0);
                    }
                } else {
                    buf.append(c);
                }
            }
        }));

        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(body);

        Transport transport = session.getTransport("smtp");
        try {
            // Connect explicitly to get better error messages on auth/connect failure
            // pass host/port/credentials explicitly
            transport.connect("smtp.gmail.com", 587, fromEmail, password);
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            try {
                transport.close();
            } catch (Exception ignored) {
            }
        }
    }
}
