package net.sijbers.dupas.scores.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import lombok.extern.slf4j.Slf4j;
import net.sijbers.dupas.scores.model.db.ImageEntity;
import net.sijbers.dupas.scores.repositories.IImageRepository;
import net.sijbers.dupas.scores.util.ImageUtil;

@Slf4j
@Service
public class FilesStorageServiceImpl implements FilesStorageService {
	
	@Autowired
	IImageRepository imageRepository;
	
	@Value("${game.image.repository}")
	private String imageLocation;	
	
	@Override
	public void init() {
		try {
			Path root = Paths.get(imageLocation);
			Files.createDirectories(root);
			log.info("root: {}", root);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize folder for upload!");
		}
	}

	@Override
	public void save(MultipartFile file,String gameId)   {
		Path root = Paths.get(imageLocation);
	
		Path gamePath = Paths.get(root.toAbsolutePath().toString(),gameId);

		log.debug("upload");

		try {
			Files.createDirectories(gamePath);
			Files.copy(file.getInputStream(), gamePath.resolve(file.getOriginalFilename()));
			
			imageRepository.save(ImageEntity.builder()
	                .name(file.getOriginalFilename())
	                .type(file.getContentType())
	                .gameId(gameId)
	                .imageData(ImageUtil.compressImage(file.getBytes())).build());			
						
		} catch (Exception e) {
			if (e instanceof FileAlreadyExistsException) {
				throw new RuntimeException("A file of that name already exists.");
			}
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@Override
	public void deleteAll() {
		Path root = Paths.get(imageLocation);
		FileSystemUtils.deleteRecursively(root.toFile());
	}
	
	@Override
	public Resource load(String gameId, String filename) {
		try {
			Path root = Paths.get(imageLocation);
			Path gamePath = Paths.get(root.toAbsolutePath().toString(),gameId);

			Path file = gamePath.resolve(filename);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}
	
	@Override
    public byte[] getImage(String gameId) {
        List<ImageEntity> images = imageRepository.findByGameId(gameId);
        if (images.size()==1) {        
        	byte[] image = ImageUtil.decompressImage(images.get(0).getImageData());
        	return image;
        }
        return null;
    }
	
	@Override
	public boolean hasImage(String gameId) {
        List<ImageEntity> images = imageRepository.findByGameId(gameId);
        if (images.size()==1) {        
        	return true;
        }
		return false;
	}

	@Override
	public void delete(String gameId) {
		Path root = Paths.get(imageLocation);
		Path gamePath = Paths.get(root.toAbsolutePath().toString(),gameId);
		FileSystemUtils.deleteRecursively(gamePath.toFile());
		
		List<ImageEntity> images = imageRepository.findByGameId(gameId);
		images.forEach(image -> {
			imageRepository.delete(image);
		});
	}
	
	@Override
	public Stream<Path> loadAll(String gameId) {
		Path root = Paths.get(imageLocation);
		Path gamePath = Paths.get(root.toAbsolutePath().toString(),gameId);
		log.info("gamePath: {}",gamePath);
		//check if exists the path
		
		if (Files.exists(gamePath)) {
			try {
				return Files.walk(gamePath, 1).filter(path -> !path.equals(gamePath)).map(gamePath::relativize);
			} catch (IOException e) {
				throw new RuntimeException("Could not load the files!");
			}
		}
		return Stream.empty();
	}
}
