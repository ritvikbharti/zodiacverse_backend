//  Same fix — match YOUR project's package
package com.ritvik.zodiacverseBackend.service;

public class WelcomeEmailTemplate {

    private WelcomeEmailTemplate() {}

    public static String build(String fullName, String clientUrl) {
        String firstName = (fullName != null && !fullName.isBlank())
                ? fullName.trim().split("\\s+")[0]
                : "Cosmic Traveller";

        return """
            <!DOCTYPE html>
            <html>
              <head>
                <meta charset="utf-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <title>Welcome to ZodiacVerse</title>
              </head>
              <body style="margin:0;padding:0;background-color:#0d0d1a;
                           font-family:'Segoe UI',Arial,sans-serif;">
                <table width="100%%" cellpadding="0" cellspacing="0"
                       style="background:#0d0d1a;padding:40px 0;">
                  <tr>
                    <td align="center">
                      <table width="600" cellpadding="0" cellspacing="0"
                        style="background:linear-gradient(135deg,#1a1a2e,#16213e);
                               border-radius:16px;overflow:hidden;
                               border:1px solid rgba(139,92,246,0.3);">

                        <!-- Header -->
                        <tr>
                          <td align="center"
                            style="background:linear-gradient(135deg,#7c3aed,#4f46e5);
                                   padding:40px 30px;">
                            <h1 style="margin:0;color:#ffffff;font-size:32px;
                                       letter-spacing:2px;">✨ ZodiacVerse</h1>
                            <p style="margin:8px 0 0;color:rgba(255,255,255,0.8);
                                      font-size:14px;">Your Personal Cosmic Guide</p>
                          </td>
                        </tr>

                        <!-- Body -->
                        <tr>
                          <td style="padding:40px 40px 20px;">
                            <h2 style="color:#e2d9f3;font-size:22px;margin:0 0 16px;">
                              Welcome aboard, %s! 🚀
                            </h2>
                            <p style="color:#a89fc2;line-height:1.8;font-size:15px;
                                      margin:0 0 20px;">
                              The stars have been waiting for you. Your ZodiacVerse
                              account is now active and your cosmic journey has
                              officially begun.
                            </p>

                            <!-- Feature Cards -->
                            <table width="100%%" cellpadding="0" cellspacing="0"
                                   style="margin:24px 0;">
                              <tr>
                                <td style="background:rgba(124,58,237,0.15);
                                    border:1px solid rgba(124,58,237,0.3);
                                    border-radius:12px;padding:16px 20px;
                                    vertical-align:top;width:48%%;">
                                  <p style="margin:0;font-size:22px;">🔮</p>
                                  <p style="margin:6px 0 4px;color:#e2d9f3;
                                            font-weight:600;font-size:14px;">Daily Horoscope</p>
                                  <p style="margin:0;color:#a89fc2;font-size:12px;">
                                    Personalized cosmic insights every morning</p>
                                </td>
                                <td style="width:4%%;"></td>
                                <td style="background:rgba(79,70,229,0.15);
                                    border:1px solid rgba(79,70,229,0.3);
                                    border-radius:12px;padding:16px 20px;
                                    vertical-align:top;width:48%%;">
                                  <p style="margin:0;font-size:22px;">🌙</p>
                                  <p style="margin:6px 0 4px;color:#e2d9f3;
                                            font-weight:600;font-size:14px;">Birth Chart</p>
                                  <p style="margin:0;color:#a89fc2;font-size:12px;">
                                    Vedic &amp; Western kundli analysis</p>
                                </td>
                              </tr>
                              <tr>
                                <td colspan="3" style="height:12px;"></td>
                              </tr>
                              <tr>
                                <td style="background:rgba(124,58,237,0.15);
                                    border:1px solid rgba(124,58,237,0.3);
                                    border-radius:12px;padding:16px 20px;
                                    vertical-align:top;width:48%%;">
                                  <p style="margin:0;font-size:22px;">⭐</p>
                                  <p style="margin:6px 0 4px;color:#e2d9f3;
                                            font-weight:600;font-size:14px;">Expert Astrologers</p>
                                  <p style="margin:0;color:#a89fc2;font-size:12px;">
                                    Book 1-on-1 sessions with verified experts</p>
                                </td>
                                <td style="width:4%%;"></td>
                                <td style="background:rgba(79,70,229,0.15);
                                    border:1px solid rgba(79,70,229,0.3);
                                    border-radius:12px;padding:16px 20px;
                                    vertical-align:top;width:48%%;">
                                  <p style="margin:0;font-size:22px;">📊</p>
                                  <p style="margin:6px 0 4px;color:#e2d9f3;
                                            font-weight:600;font-size:14px;">AI Reports</p>
                                  <p style="margin:0;color:#a89fc2;font-size:12px;">
                                    Detailed cosmic reports powered by AI</p>
                                </td>
                              </tr>
                            </table>

                            <!-- CTA Button -->
                            <table width="100%%" cellpadding="0" cellspacing="0"
                                   style="margin:30px 0;">
                              <tr>
                                <td align="center">
                                  <a href="%s/dashboard"
                                    style="display:inline-block;
                                           background:linear-gradient(135deg,#7c3aed,#4f46e5);
                                           color:#ffffff;text-decoration:none;
                                           padding:14px 40px;border-radius:50px;
                                           font-weight:600;font-size:15px;
                                           letter-spacing:0.5px;">
                                     Explore Your Dashboard
                                  </a>
                                </td>
                              </tr>
                            </table>

                            <p style="color:#7c6f9a;font-size:13px;line-height:1.6;">
                              If you didn't create this account, you can safely
                              ignore this email.
                            </p>
                          </td>
                        </tr>

                        <!-- Footer -->
                        <tr>
                          <td style="background:rgba(0,0,0,0.3);padding:24px 40px;
                              text-align:center;
                              border-top:1px solid rgba(139,92,246,0.2);">
                            <p style="color:#5a4f7a;font-size:12px;margin:0;">
                              © 2025 ZodiacVerse · Made with ✨ for cosmic explorers
                            </p>
                            <p style="margin:8px 0 0;">
                              <a href="#"
                                 style="color:#7c3aed;font-size:12px;
                                        text-decoration:none;margin:0 8px;">
                                Privacy Policy</a>
                              <a href="#"
                                 style="color:#7c3aed;font-size:12px;
                                        text-decoration:none;margin:0 8px;">
                                Unsubscribe</a>
                            </p>
                          </td>
                        </tr>

                      </table>
                    </td>
                  </tr>
                </table>
              </body>
            </html>
            """.formatted(firstName, clientUrl);
    }
}