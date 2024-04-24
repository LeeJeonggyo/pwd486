package univ.inu.Capstone.common.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    public void sendNotification(String deviceId, String title, String message) {
        // 1. 메시지 생성
        Message fcmMessage = Message.builder()
                .setToken(deviceId) // 대상 디바이스의 토큰
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(message)
                                .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(fcmMessage);
            log.info("=========================================================================================");
            log.info("Successfully sent message: {}", response);
            log.info("=========================================================================================");
        } catch (FirebaseMessagingException e) {
            log.info("=========================================================================================");
            log.info("Error sending message: {}", e.getMessage());
            log.info("=========================================================================================");
        }
    }
}
