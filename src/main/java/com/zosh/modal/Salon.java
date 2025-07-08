package com.zosh.modal;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "salons")
@Data
public class Salon {

        /* ---------- PK con secuencia Oracle ---------- */
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "salon_seq")
        @SequenceGenerator(name = "salon_seq", sequenceName = "salon_sequence", allocationSize = 1)
        private Long id;

        /* ---------- Datos básicos ---------- */
        @Column(nullable = false, length = 100)
        private String name;

        /*
         * List<String> funciona en Oracle; se crea tabla intermedia
         * SALON_IMAGES (SALON_ID, IMAGES)
         */
        @ElementCollection
        @CollectionTable(name = "salon_images", joinColumns = @JoinColumn(name = "salon_id"))
        @Column(name = "image", length = 400)
        private List<String> images;

        @Column(nullable = false, length = 255)
        private String address;

        @Column(nullable = false, length = 15)
        private String phoneNumber;

        @Column(nullable = false, length = 255)
        private String email;

        @Column(nullable = false, length = 50)
        private String city;

        /* ---------- Flags booleanos (NUMBER(1)) ---------- */
        @Column(name = "is_open", nullable = false, precision = 1)
        private Integer isOpen; // 1 = true, 0 = false

        @Column(name = "home_service", nullable = false, precision = 1)
        private Integer homeService;

        @JdbcTypeCode(SqlTypes.BOOLEAN) // Hibernate 6+
        @Column(name = "active", nullable = false)
        private boolean active;

        /* ---------- Resto de campos ---------- */
        @Column(name = "owner_id", nullable = false)
        private Long ownerId;

        @Column(name = "open_time", nullable = false)
        private LocalTime openTime;

        @Column(name = "close_time", nullable = false)
        private LocalTime closeTime;

        /*
         * =======================================================
         * Métodos proxy para NO romper tu código existente
         * =======================================================
         */

        /* ----- isOpen ----- */
        public boolean isOpen() {
                return isOpen != null && isOpen == 1;
        }

        public void setOpen(boolean v) {
                this.isOpen = v ? 1 : 0;
        }

        /* ----- homeService ----- */
        public boolean isHomeService() {
                return homeService != null && homeService == 1;
        }

        public void setHomeService(boolean v) {
                this.homeService = v ? 1 : 0;
        }

}
