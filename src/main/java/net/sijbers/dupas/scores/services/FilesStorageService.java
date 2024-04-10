package net.sijbers.dupas.scores.services;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;

public interface FilesStorageService {
	  public void init();

	  public void save(MultipartFile file,String gameId);

	  public Resource load(String gameId, String filename);

	  public void deleteAll();
	  
	  public void delete(String gameId);

	  public Stream<Path> loadAll(String gameID);
	  
	  public byte[] getImage(String gameId);

	  public boolean hasImage(String gameId);
}
