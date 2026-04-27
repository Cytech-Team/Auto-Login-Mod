package cloud.sect.alm.cloud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import cloud.sect.alm.AutoLoginModClient;

public class IdentityManager {
    
    public enum AccountStatus {
        VERIFIED_MOJANG, // ไอดีแท้ (Microsoft)
        LOCAL_ACCOUNT,   // ไอดีเถื่อน/Local
        UNTRUSTED        // ไม่สามารถระบุได้
    }

    public static AccountStatus checkIdentity() {
        Minecraft mc = Minecraft.getInstance();
        User user = mc.getUser();
        
        // เช็คผ่าน Session Type ของ Minecraft Native
        // ถ้าเป็น User.Type.MS หรือ MOJANG คือไอดีแท้ที่ผ่านการ Auth
        if (user.getType() == User.Type.MS || user.getType() == User.Type.MOJANG) {
            AutoLoginModClient.LOGGER.info("§6[ALM-Cloud] §aIdentity Verified via Microsoft/Mojang.");
            return AccountStatus.VERIFIED_MOJANG;
        }
        
        AutoLoginModClient.LOGGER.warn("§6[ALM-Cloud] §cUnverified Local Account detected. Cloud Sync Disabled.");
        return AccountStatus.LOCAL_ACCOUNT;
    }

    public static boolean canUseCloud() {
        // เงื่อนไข: ต้องเป็นไอดีแท้เท่านั้นถึงจะใช้ระบบเมฆได้
        return checkIdentity() == AccountStatus.VERIFIED_MOJANG;
    }
}
