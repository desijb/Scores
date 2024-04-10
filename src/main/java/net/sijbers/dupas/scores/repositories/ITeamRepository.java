package net.sijbers.dupas.scores.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sijbers.dupas.scores.model.db.TeamEntity;

public interface ITeamRepository  extends JpaRepository<TeamEntity,Long>{

}
