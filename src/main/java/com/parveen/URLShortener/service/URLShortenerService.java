package com.parveen.URLShortener.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.parveen.URLShortener.entity.UrlStore;
import com.parveen.URLShortener.exception.URLShortenerException;
import com.parveen.URLShortener.repository.UrlShortenerRepository;
import com.parveen.URLShortener.utility.Base62;
			
import jakarta.transaction.Transactional;

@Service
@Transactional
public class URLShortenerService {
	
	@Autowired
	private UrlShortenerRepository urlShortenerRepository;
	@Autowired
	private Base62 base62;
	
	public String generateShortUrl(String longUrl) throws URLShortenerException {
	    Optional<UrlStore> urlOptional = urlShortenerRepository.findBylongurl(longUrl);
		if(!urlOptional.isEmpty()) throw new URLShortenerException("Already Exist URL");
		UrlStore urlStore = new UrlStore(longUrl);
		UrlStore urlStore2 = urlShortenerRepository.save(urlStore);
		return base62.encodeBase62(urlStore2.getShortId());
	}
	
	public String fetchLongUrl(String shortUrl) throws URLShortenerException {
		long shortcoded = base62.decodeBase62(shortUrl);
		Optional<UrlStore> urlStoreOptional = urlShortenerRepository.findById(shortcoded);
		if(!urlStoreOptional.isPresent()) {
			throw new URLShortenerException("URL_DOES_NOT_EXIST");
		}
		return urlStoreOptional.get().getLongurl();
	}
	
	
}