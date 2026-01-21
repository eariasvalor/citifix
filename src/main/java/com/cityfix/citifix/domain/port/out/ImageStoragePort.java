package com.cityfix.citifix.domain.port.out;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ImageStoragePort {
    String upload(MultipartFile file) throws IOException;
}
