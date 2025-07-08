// =============================================================================
// SALON REPOSITORY - Compatible con Oracle
// src/main/java/com/zosh/repository/SalonRepository.java
// =============================================================================
package com.zosh.repository;

import com.zosh.modal.Salon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalonRepository extends JpaRepository<Salon, Long> {

    // Método existente - funciona bien
    Salon findByOwnerId(Long ownerId);

    // Método para verificar si existe salón por owner
    boolean existsByOwnerId(Long ownerId);

    // 🚀 MÉTODO CORREGIDO PARA ORACLE
    @Query("SELECT s FROM Salon s WHERE " +
            "(UPPER(s.city) LIKE UPPER(CONCAT('%', :keyword, '%')) OR " +
            "UPPER(s.name) LIKE UPPER(CONCAT('%', :keyword, '%')) OR " +
            "UPPER(s.address) LIKE UPPER(CONCAT('%', :keyword, '%'))) AND " +
            "s.active = 1")
    List<Salon> searchSalons(@Param("keyword") String keyword);

    // 🚀 MÉTODOS ADICIONALES PARA ORACLE

    /**
     * Busca salones activos por ciudad exacta
     */
    @Query("SELECT s FROM Salon s WHERE UPPER(s.city) = UPPER(:city) AND s.active = 1")
    List<Salon> findActiveSalonsByCity(@Param("city") String city);

    /**
     * Busca salones por nombre exacto
     */
    @Query("SELECT s FROM Salon s WHERE UPPER(s.name) = UPPER(:name) AND s.active = 1")
    List<Salon> findActiveSalonsByName(@Param("name") String name);

    /**
     * Obtiene todos los salones activos
     */
    @Query("SELECT s FROM Salon s WHERE s.active = 1 ORDER BY s.name")
    List<Salon> findAllActiveSalons();

    /**
     * Busca salones por owner que estén activos
     */
    @Query("SELECT s FROM Salon s WHERE s.ownerId = :ownerId AND s.active = 1")
    List<Salon> findActiveSalonsByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * Cuenta salones activos por ciudad
     */
    @Query("SELECT COUNT(s) FROM Salon s WHERE UPPER(s.city) = UPPER(:city) AND s.active = 1")
    Long countActiveSalonsByCity(@Param("city") String city);
}