package org.koulibrary.koulibraryreservationapp.domains;

import org.koulibrary.koulibraryreservationapp.dtos.responses.NotificationContent;

public final class AuthMessages {
    private AuthMessages() {}

    public static NotificationContent emailVerification(String code, int minutes) {
        return new NotificationContent(
                "E-posta doğrulama kodunuz",
                "Kaydınızı tamamlamak için doğrulama kodunuz: " + code + "\n"
                        + "Bu kod " + minutes + " dakika boyunca geçerlidir.");
    }

    public static NotificationContent passwordReset(String code, int minutes) {
        return new NotificationContent(
                "Şifre sıfırlama kodunuz",
                "Şifrenizi sıfırlamak için kodunuz: " + code + "\n"
                        + "Bu kod " + minutes + " dakika boyunca geçerlidir. Bu işlemi siz başlatmadıysanız dikkate almayın.");
    }
}
