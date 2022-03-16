package de.thkoeln.syp.team17.backend.services;

import de.thkoeln.syp.team17.backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

        @Autowired
        private JavaMailSender emailSender;

        public void sendVerificationToken(User user) throws MailException {
            SimpleMailMessage message = new SimpleMailMessage();
            // TODO: Make configurable
            message.setFrom("admin@iot-device-tracker.com");
            message.setTo(user.getEmail());
            message.setSubject("Please verify your e-mail address");
            message.setText("Your verification code: " + user.getVerificationToken()); // TODO: More comprehensive message
            emailSender.send(message);
        }

}
