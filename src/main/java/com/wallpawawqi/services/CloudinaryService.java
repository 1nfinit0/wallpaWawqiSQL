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
    try {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(
            base64,
            ObjectUtils.asMap(
                "public_id", "productos/" + publicId.replaceAll(" ", "_"),
                "resource_type", "auto",
                "quality", "auto"
            )
        );
        String url = (String) uploadResult.get("secure_url");
        System.out.println("Imagen subida a: " + url);
        return url;
    } catch (Exception e) {
        System.err.println("Error en Cloudinary: " + e.getMessage());
        e.printStackTrace();
        throw e;
    }
}
}
