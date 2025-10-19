package com.parveen.URLShortener.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parveen.URLShortener.entity.UrlStore;

public interface UrlShortenerRepository extends JpaRepository<UrlStore, Long> {
	Optional<UrlStore> findBylongurl(String name);
}