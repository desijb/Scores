package net.sijbers.dupas.scores.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sijbers.dupas.scores.model.db.PlayerEntity;

public interface IPlayerRepository  extends JpaRepository<PlayerEntity,Long>{
	List<PlayerEntity>  findByActive(boolean active);
	List<PlayerEntity>  findByGuestPlayer(boolean guestPlayer);
}
