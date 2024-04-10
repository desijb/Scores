package net.sijbers.dupas.scores.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sijbers.dupas.scores.model.db.SecretEntity;

public interface ISecretRepository extends JpaRepository<SecretEntity,Long> {
	List<SecretEntity> findBySecretType(String secretType);
}
