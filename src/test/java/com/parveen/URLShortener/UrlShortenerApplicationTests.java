package com.parveen.URLShortener;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.parveen.URLShortener.entity.UrlStore;
import com.parveen.URLShortener.exception.URLShortenerException;
import com.parveen.URLShortener.repository.UrlShortenerRepository;
import com.parveen.URLShortener.service.URLShortenerService;
import com.parveen.URLShortener.utility.Base62;

@SpringBootTest
class UrlShortenerApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@Mock
    private UrlShortenerRepository urlShortenerRepository;

    @Mock
    private Base62 base62;

    @InjectMocks
    private URLShortenerService urlShortenerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateShortUrl_Success() throws Exception {
        String longUrl = "https://example.com";
        UrlStore urlStore = new UrlStore(longUrl);
        urlStore.setShortId(100L);

        when(urlShortenerRepository.findBylongurl(longUrl)).thenReturn(Optional.empty());
        when(urlShortenerRepository.save(any(UrlStore.class))).thenReturn(urlStore);
        when(base62.encodeBase62(100L)).thenReturn("abc123");

        String result = urlShortenerService.generateShortUrl(longUrl);

        assertEquals("abc123", result);
        verify(urlShortenerRepository).save(any(UrlStore.class));
    }

    @Test
    public void testGenerateShortUrl_AlreadyExists() {
        String longUrl = "https://example.com";
        UrlStore existingUrl = new UrlStore(longUrl);

        when(urlShortenerRepository.findBylongurl(longUrl)).thenReturn(Optional.of(existingUrl));

        URLShortenerException exception = assertThrows(URLShortenerException.class, () -> {
            urlShortenerService.generateShortUrl(longUrl);
        });

        assertEquals("Already Exist URL", exception.getMessage());
        verify(urlShortenerRepository, never()).save(any());
    }

    @Test
    public void testFetchLongUrl_Success() throws Exception {
        String shortUrl = "abc123";
        long shortId = 100L;
        String longUrl = "https://example.com";
        UrlStore urlStore = new UrlStore(longUrl);
        urlStore.setShortId(shortId);

        when(base62.decodeBase62(shortUrl)).thenReturn(shortId);
        when(urlShortenerRepository.findById(shortId)).thenReturn(Optional.of(urlStore));

        String result = urlShortenerService.fetchLongUrl(shortUrl);

        assertEquals(longUrl, result);
        verify(urlShortenerRepository).findById(shortId);
    }

    @Test
    public void testFetchLongUrl_NotFound() {
        String shortUrl = "abc123";
        long shortId = 100L;

        when(base62.decodeBase62(shortUrl)).thenReturn(shortId);
        when(urlShortenerRepository.findById(shortId)).thenReturn(Optional.empty());

        URLShortenerException exception = assertThrows(URLShortenerException.class, () -> {
            urlShortenerService.fetchLongUrl(shortUrl);
        });

        assertEquals("URL_DOES_NOT_EXIST", exception.getMessage());
    }
	
}