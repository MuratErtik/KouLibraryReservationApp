package org.koulibrary.koulibraryreservationapp.domains;

import org.koulibrary.koulibraryreservationapp.dtos.responses.NotificationContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class NotificationMessages {

    private NotificationMessages() {}

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static NotificationContent noShowPenalty(LocalDateTime penaltyEnd) {
        return new NotificationContent(
                "Rezervasyonunuz kaçırıldı",
                "Belirtilen süre içinde check-in yapmadığınız için rezervasyonunuz iptal edildi.\n"
                        + "Bu nedenle hesabınıza ceza uygulandı. " + penaltyEnd.format(DT)
                        + " tarihine kadar yeni rezervasyon yapamayacaksınız.");
    }

    public static NotificationContent manualPenalty(LocalDateTime penaltyEnd, String description) {
        String desc = (description != null && !description.isBlank()) ? description + "\n" : "";
        return new NotificationContent(
                "Hesabınıza ceza uygulandı",
                "Yöneticiniz hesabınıza bir ceza uyguladı.\n" + desc
                        + "Ceza " + penaltyEnd.format(DT) + " tarihine kadar geçerlidir. "
                        + "Bu süre boyunca rezervasyon yapamayacaksınız.");
    }

    public static NotificationContent adminCancelled(Integer deskNumber, LocalDateTime startTime, String reason) {
        String r = (reason != null && !reason.isBlank()) ? "Gerekçe: " + reason : "";
        return new NotificationContent(
                "Rezervasyonunuz iptal edildi",
                deskNumber + " numaralı masa için " + startTime.format(DT)
                        + " tarihli rezervasyonunuz yönetici tarafından iptal edildi.\n" + r);
    }

    public static NotificationContent checkInReminder(String saloonName, Integer deskNumber, LocalDateTime startTime) {
        return new NotificationContent(
                "Rezervasyon hatırlatması",
                saloonName + " salonundaki " + deskNumber + " numaralı masa rezervasyonunuz "
                        + startTime.format(DT) + " saatinde başlıyor.\n"
                        + "Masadaki QR kodu okutarak check-in yapmayı unutmayın.");
    }
}
