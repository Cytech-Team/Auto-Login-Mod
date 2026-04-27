package cloud.sect.alm.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class AuthDetector {
    
    // Patterns for login commands
    private static final Pattern[] LOGIN_PATTERNS = {
        Pattern.compile("(?i)/login\\s+<password>"),
        Pattern.compile("(?i)/login\\s+\\[password\\]"),
        Pattern.compile("(?i)Please\\s+login"),
        Pattern.compile("(?i)type\\s+/login"),
        Pattern.compile("(?i)use\\s+/login"),
        Pattern.compile("(?i)เข้าสู่ระบบด้วย\\s+/login"),
        Pattern.compile("(?i)กรุณาพิมพ์\\s+/login"),
        Pattern.compile("(?i)ใช้คำสั่ง\\s+/login")
    };
    
    // Patterns for register commands
    private static final Pattern[] REGISTER_PATTERNS = {
        Pattern.compile("(?i)/register\\s+<password>"),
        Pattern.compile("(?i)/register\\s+\\[password\\]"),
        Pattern.compile("(?i)Please\\s+register"),
        Pattern.compile("(?i)type\\s+/register"),
        Pattern.compile("(?i)use\\s+/register"),
        Pattern.compile("(?i)สมัครสมาชิกด้วย\\s+/register"),
        Pattern.compile("(?i)กรุณาพิมพ์\\s+/register"),
        Pattern.compile("(?i)ใช้คำสั่ง\\s+/register")
    };

    // Patterns for successful login
    private static final Pattern[] SUCCESS_PATTERNS = {
        Pattern.compile("(?i)Login\\s+successful"),
        Pattern.compile("(?i)Successfully\\s+logged\\s+in"),
        Pattern.compile("(?i)เข้าสู่ระบบสำเร็จ"),
        Pattern.compile("(?i)ยินดีต้อนรับกลับ"),
        Pattern.compile("(?i)You\\s+are\\s+already\\s+logged\\s+in"),
        Pattern.compile("(?i)คุณเข้าสู่ระบบอยู่แล้ว")
    };
    
    // Generic patterns for any command that looks like it's asking for a password
    private static final Pattern GENERIC_AUTH_PATTERN = Pattern.compile("(?i)/(login|l|reg|register|auth)\\s+");

    public enum AuthType {
        NONE,
        LOGIN,
        REGISTER,
        SUCCESS
    }

    public static AuthType detect(String message) {
        if (message == null) return AuthType.NONE;
        
        String cleanMessage = message.replaceAll("§.", ""); 
        String lower = cleanMessage.toLowerCase();

        // 1. Success Patterns (Hardcoded for common servers)
        if (lower.contains("login successful") || lower.contains("เข้าสู่ระบบสำเร็จ") || lower.contains("สำเร็จแล้ว") || lower.contains("logged in")) {
            return AuthType.SUCCESS;
        }

        // 2. Custom Triggers (User-defined)
        for (String trigger : cloud.sect.alm.config.ServerConfig.getCustomTriggers()) {
            if (lower.contains(trigger.toLowerCase())) {
                // Determine if it should be login or register based on keywords
                if (trigger.contains("reg")) return AuthType.REGISTER;
                return AuthType.LOGIN;
            }
        }

        // 3. Core Commands (Simplified as requested)
        if (lower.contains("/register") || lower.contains("/reg")) {
            return AuthType.REGISTER;
        }
        if (lower.contains("/login") || lower.contains("/l ") || lower.contains("/auth")) {
            return AuthType.LOGIN;
        }
        
        // 4. Fallback Patterns (For legacy/common servers)
        for (Pattern p : REGISTER_PATTERNS) {
            if (p.matcher(cleanMessage).find()) return AuthType.REGISTER;
        }
        for (Pattern p : LOGIN_PATTERNS) {
            if (p.matcher(cleanMessage).find()) return AuthType.LOGIN;
        }

        return AuthType.NONE;
    }
}
