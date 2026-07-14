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

    public static String uploadImage(byte[] imageBytes, String publicId) throws Exception {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(
            imageBytes,
            ObjectUtils.asMap(
                "public_id", "productos/" + publicId,
                "resource_type", "auto",
                "quality", "auto"
            )
        );
        return (String) uploadResult.get("secure_url");
    }
}
