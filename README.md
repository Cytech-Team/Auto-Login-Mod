# 🛡️ Auto-Login Mod (Pro Edition)

![Mod Icon](src/main/resources/assets/auto-login-mod/icon.png)

The ultimate client-side utility for Minecraft players who want a seamless and secure login experience on servers with AuthMe or similar systems.

## 🚀 Key Features

*   **⚡ Smart Auto-Login:** Automatically detects `/login` or `/register` prompts and submits your credentials.
*   **🛠️ Custom Triggers:** Add your own keywords to trigger auto-login via `/alm trigger add <word>`.
*   **🛡️ Pro Spam Protection:** Integrated cooldown system prevents chat spam even on slow servers.
*   **🔒 Military-Grade Security:**
    *   PBKDF2 with 300,000 iterations for key derivation.
    *   AES-GCM (256-bit) encryption for stored passwords.
    *   Secure Master Password lock (F9 to unlock).
*   **⏳ Gaussian Stealth Delay:** Mimics human typing speed with randomized delays (800ms - 2000ms).
*   **🌐 Full Thai Support:** Works perfectly with Thai servers and custom Thai login messages.

## 🎮 Commands

| Command | Description |
| :--- | :--- |
| `/alm gui` | Open the configuration screen |
| `/alm trigger add <word>` | Add a custom keyword to trigger login |
| `/alm trigger list` | See all active custom triggers |
| `/alm set <password>` | Set a global password for all servers |
| `/alm add <ip> <pass>` | Set a specific password for a server IP |
| `/alm list` | View all saved server configurations |

## ⚙️ How to Use

1.  Press **F9** in-game to set your **Master Password**.
2.  Use `/alm set <your_password>` for a global login.
3.  Join any server and enjoy the magic!

---
*Created with ❤️ by Cytech Team & namnarak*
