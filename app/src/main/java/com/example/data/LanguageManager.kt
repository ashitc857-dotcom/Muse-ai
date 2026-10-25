package com.example.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String, val label: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    BANGLA("bn", "Bangla", "বাংলা"),
    HINDI("hi", "Hindi", "हिंदी")
}

object Strings {
    var currentLanguage by mutableStateOf(AppLanguage.ENGLISH)

    private val translations = mapOf(
        "app_title" to mapOf(
            AppLanguage.ENGLISH to "Muse AI Studio",
            AppLanguage.BANGLA to "মিউজ এআই স্টুডিও",
            AppLanguage.HINDI to "म्यूज़ एआई स्टूडियो"
        ),
        "tagline" to mapOf(
            AppLanguage.ENGLISH to "Next-Gen AI Video, Music & Creative Studio",
            AppLanguage.BANGLA to "নেক্সট-জেন এআই ভিডিও, মিউজিক ও ক্রিয়েটিভ স্টুডিও",
            AppLanguage.HINDI to "नेक्स्ट-जेन एआई वीडियो, संगीत और रचनात्मक स्टूडियो"
        ),
        "tokens" to mapOf(
            AppLanguage.ENGLISH to "Tokens",
            AppLanguage.BANGLA to "টোকেন",
            AppLanguage.HINDI to "टोकन"
        ),
        "daily_bonus" to mapOf(
            AppLanguage.ENGLISH to "Daily Bonus",
            AppLanguage.BANGLA to "দৈনিক বোনাস",
            AppLanguage.HINDI to "दैनिक बोनस"
        ),
        "claim_daily" to mapOf(
            AppLanguage.ENGLISH to "Claim 10,000 Tokens",
            AppLanguage.BANGLA to "১০,০০০ টোকেন দাবি করুন",
            AppLanguage.HINDI to "10,000 टोकन का दावा करें"
        ),
        "claimed" to mapOf(
            AppLanguage.ENGLISH to "Claimed Today",
            AppLanguage.BANGLA to "আজ গ্রহণ করা হয়েছে",
            AppLanguage.HINDI to "आज प्राप्त किया"
        ),
        "nav_home" to mapOf(
            AppLanguage.ENGLISH to "Studio",
            AppLanguage.BANGLA to "স্টুডিও",
            AppLanguage.HINDI to "स्टूडियो"
        ),
        "nav_video" to mapOf(
            AppLanguage.ENGLISH to "Video",
            AppLanguage.BANGLA to "ভিডিও",
            AppLanguage.HINDI to "वीडियो"
        ),
        "nav_music" to mapOf(
            AppLanguage.ENGLISH to "Music",
            AppLanguage.BANGLA to "গান/অডিও",
            AppLanguage.HINDI to "संगीत"
        ),
        "nav_image" to mapOf(
            AppLanguage.ENGLISH to "Image",
            AppLanguage.BANGLA to "ছবি",
            AppLanguage.HINDI to "चित्र"
        ),
        "nav_chat" to mapOf(
            AppLanguage.ENGLISH to "AI Agent",
            AppLanguage.BANGLA to "এআই এজেন্ট",
            AppLanguage.HINDI to "एआई एजेंट"
        ),
        "nav_projects" to mapOf(
            AppLanguage.ENGLISH to "Projects",
            AppLanguage.BANGLA to "প্রকল্প",
            AppLanguage.HINDI to "प्रोजेक्ट्स"
        ),
        "generate_video" to mapOf(
            AppLanguage.ENGLISH to "Generate AI Video",
            AppLanguage.BANGLA to "এআই ভিডিও তৈরি করুন",
            AppLanguage.HINDI to "एआई वीडियो बनाएं"
        ),
        "prompt_hint_video" to mapOf(
            AppLanguage.ENGLISH to "Describe your cinematic scene, camera angle, action...",
            AppLanguage.BANGLA to "সিনেমাটিক দৃশ্য, ক্যামেরার কোণ এবং চরিত্রের অ্যাকশন লিখুন...",
            AppLanguage.HINDI to "अपने सिनेमाई दृश्य, कैमरा एंगल और क्रिया का वर्णन करें..."
        ),
        "prompt_hint_music" to mapOf(
            AppLanguage.ENGLISH to "Describe mood, genre, rhythm or write song topic...",
            AppLanguage.BANGLA to "গানের মেজাজ, ধরণ, সুর বা বিষয়বস্তু লিখুন...",
            AppLanguage.HINDI to "संगीत का मिज़ाज, शैली, सुर या विषय लिखें..."
        ),
        "prompt_hint_image" to mapOf(
            AppLanguage.ENGLISH to "Describe the image, lighting, character, 3D style...",
            AppLanguage.BANGLA to "ছবির বর্ণনা, লাইটিং, চরিত্র এবং স্টাইল লিখুন...",
            AppLanguage.HINDI to "चित्र, लाइटिंग, पात्र और स्टाइल का वर्णन करें..."
        ),
        "model_tier" to mapOf(
            AppLanguage.ENGLISH to "Model Tier",
            AppLanguage.BANGLA to "মডেল টিয়ার",
            AppLanguage.HINDI to "मॉडल टियर"
        ),
        "aspect_ratio" to mapOf(
            AppLanguage.ENGLISH to "Aspect Ratio",
            AppLanguage.BANGLA to "অনুপাত (Aspect Ratio)",
            AppLanguage.HINDI to "आस्पेक्ट रेशियो"
        ),
        "duration" to mapOf(
            AppLanguage.ENGLISH to "Duration",
            AppLanguage.BANGLA to "স্থিতিকাল",
            AppLanguage.HINDI to "अवधि"
        ),
        "resolution" to mapOf(
            AppLanguage.ENGLISH to "Resolution",
            AppLanguage.BANGLA to "রেজোলিউশন",
            AppLanguage.HINDI to "रिज़ॉल्यूशन"
        ),
        "frame_rate" to mapOf(
            AppLanguage.ENGLISH to "Frame Rate",
            AppLanguage.BANGLA to "ফ্রেম রেট",
            AppLanguage.HINDI to "फ्रेम दर"
        ),
        "generate_now" to mapOf(
            AppLanguage.ENGLISH to "Generate Now (Cost: 1 Token)",
            AppLanguage.BANGLA to "এখন তৈরি করুন (খরচ: ১ টোকেন)",
            AppLanguage.HINDI to "अभी उत्पन्न करें (लागत: 1 टोकन)"
        ),
        "generating" to mapOf(
            AppLanguage.ENGLISH to "Rendering AI Studio Production...",
            AppLanguage.BANGLA to "এআই স্টুডিও প্রডাকশন তৈরি হচ্ছে...",
            AppLanguage.HINDI to "एआई स्टूडियो प्रोडक्शन तैयार हो रहा है..."
        ),
        "enhance_prompt" to mapOf(
            AppLanguage.ENGLISH to "Enhance Prompt",
            AppLanguage.BANGLA to "প্রম্পট উন্নত করুন",
            AppLanguage.HINDI to "प्रॉम्प्ट बेहतर करें"
        ),
        "play_preview" to mapOf(
            AppLanguage.ENGLISH to "Play Preview",
            AppLanguage.BANGLA to "প্রিভিউ প্লে করুন",
            AppLanguage.HINDI to "प्रीव्यू चलाएं"
        ),
        "pause_preview" to mapOf(
            AppLanguage.ENGLISH to "Pause",
            AppLanguage.BANGLA to "থামান",
            AppLanguage.HINDI to "रोकें"
        ),
        "download_export" to mapOf(
            AppLanguage.ENGLISH to "Download & Export",
            AppLanguage.BANGLA to "ডাউনলোড ও এক্সপোর্ট",
            AppLanguage.HINDI to "डाउनलोड व निर्यात"
        ),
        "speech_to_text" to mapOf(
            AppLanguage.ENGLISH to "Voice Input",
            AppLanguage.BANGLA to "ভয়েস ইনপুট",
            AppLanguage.HINDI to "वॉइस इनपुट"
        ),
        "text_to_speech" to mapOf(
            AppLanguage.ENGLISH to "Speak Dialogue (TTS)",
            AppLanguage.BANGLA to "সংলাপ শুনুন (TTS)",
            AppLanguage.HINDI to "संवाद सुनें (TTS)"
        ),
        "record_voice" to mapOf(
            AppLanguage.ENGLISH to "Record Voice",
            AppLanguage.BANGLA to "ভয়েস রেকর্ড করুন",
            AppLanguage.HINDI to "आवाज़ रिकॉर्ड करें"
        ),
        "instrumental_synth" to mapOf(
            AppLanguage.ENGLISH to "Synthesize Music (WAV)",
            AppLanguage.BANGLA to "মিউজিক সিন্থেসাইজ (WAV)",
            AppLanguage.HINDI to "संगीत सिंथेसाइज़ (WAV)"
        ),
        "balance_history" to mapOf(
            AppLanguage.ENGLISH to "Token History",
            AppLanguage.BANGLA to "টোকেন ইতিহাস",
            AppLanguage.HINDI to "टोकन इतिहास"
        ),
        "admin_dashboard" to mapOf(
            AppLanguage.ENGLISH to "Admin & Engine Settings",
            AppLanguage.BANGLA to "অ্যাডমিন ও ইঞ্জিন সেটিংস",
            AppLanguage.HINDI to "एडमिन और इंजन सेटिंग्स"
        ),
        "demo_vs_live" to mapOf(
            AppLanguage.ENGLISH to "Live Working AI Connected",
            AppLanguage.BANGLA to "সরাসরি এআই সংযুক্ত আছে",
            AppLanguage.HINDI to "लाइव एआई कनेक्टेड है"
        ),
        "account_profile" to mapOf(
            AppLanguage.ENGLISH to "Account & Profile",
            AppLanguage.BANGLA to "অ্যাকাউন্ট ও প্রোফাইল",
            AppLanguage.HINDI to "अकाउंट और प्रोफ़ाइल"
        ),
        "sign_in" to mapOf(
            AppLanguage.ENGLISH to "Sign In",
            AppLanguage.BANGLA to "সাইন ইন",
            AppLanguage.HINDI to "साइन इन"
        ),
        "sign_up" to mapOf(
            AppLanguage.ENGLISH to "Create Account",
            AppLanguage.BANGLA to "অ্যাকাউন্ট তৈরি করুন",
            AppLanguage.HINDI to "अकाउंट बनाएं"
        ),
        "guest_mode" to mapOf(
            AppLanguage.ENGLISH to "Continue as VIP Guest",
            AppLanguage.BANGLA to "ভিআইপি অতিথি হিসেবে প্রবেশ",
            AppLanguage.HINDI to "वीआईपी गेस्ट के रूप में जारी रखें"
        ),
        "scriptwriter" to mapOf(
            AppLanguage.ENGLISH to "Screenplay & Story Studio",
            AppLanguage.BANGLA to "স্ক্রিপ্ট ও গল্প স্টুডিও",
            AppLanguage.HINDI to "स्क्रिप्ट और कहानी स्टूडियो"
        ),
        "lyrics_generator" to mapOf(
            AppLanguage.ENGLISH to "AI Song Lyrics",
            AppLanguage.BANGLA to "এআই গানের লিরিক্স",
            AppLanguage.HINDI to "एआई गाने के बोल"
        ),
        "scene_breakdown" to mapOf(
            AppLanguage.ENGLISH to "Scene Breakdown",
            AppLanguage.BANGLA to "দৃশ্য বিভাজন",
            AppLanguage.HINDI to "दृश्य विश्लेषण"
        )
    )

    fun get(key: String): String {
        return translations[key]?.get(currentLanguage) ?: translations[key]?.get(AppLanguage.ENGLISH) ?: key
    }
}
