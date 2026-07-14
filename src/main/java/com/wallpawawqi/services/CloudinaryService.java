package com.wallpawawqi.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.Map;

public class CloudinaryService {
    private static Cloudinary cloudinary;

    static {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "etx7nfjt",
            "api_key", "955592884628473",
            "api_secret", "Bz-JRMI7Qy_FSd9RleNgbg6DAww"
        ));
    }

    public static String uploadImageFromBase64(String base64, String publicId) throws Exception {
        // Cloudinary puede recibir base64 directamente
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(
            base64,
            ObjectUtils.asMap(
                "public_id", "productos/" + publicId.replaceAll(" ", "_"),
                "resource_type", "auto",
                "quality", "auto"
            )
        );
        return (String) uploadResult.get("secure_url");
    }
}
