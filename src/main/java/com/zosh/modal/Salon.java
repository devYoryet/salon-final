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

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "salon_seq")
        @SequenceGenerator(name = "salon_seq", sequenceName = "salon_sequence", allocationSize = 1)
        private Long id;

        @Column(nullable = false, length = 100)
        private String name;

        @ElementCollection
        @CollectionTable(name = "salon_images", joinColumns = @JoinColumn(name = "salon_id"))
        @Column(name = "image", length = 400)
        private List<String> images;

        @Column(nullable = false, length = 255)
        private String address;

        @Column(name = "phone_number", nullable = true, length = 15) // 🚀 CAMBIAR A nullable = true
        private String phoneNumber;

        @Column(nullable = false, length = 255)
        private String email;

        @Column(nullable = false, length = 50)
        private String city;

        // 🚀 CAMPOS BOOLEANOS PARA ORACLE
        @Column(name = "is_open", nullable = false, precision = 1)
        private Integer isOpen; // 1 = true, 0 = false

        @Column(name = "home_service", nullable = false, precision = 1)
        private Integer homeService; // 1 = true, 0 = false

        @Column(name = "active", nullable = false, precision = 1)
        private Integer active; // 1 = true, 0 = false

        @Column(name = "owner_id", nullable = false)
        private Long ownerId;

        @Column(name = "open_time", nullable = false)
        private LocalTime openTime;

        @Column(name = "close_time", nullable = false)
        private LocalTime closeTime;

        // 🚀 MÉTODOS PROXY PARA COMPATIBILIDAD
        public boolean isOpen() {
                return isOpen != null && isOpen == 1;
        }

        public void setOpen(boolean v) {
                this.isOpen = v ? 1 : 0;
        }

        public boolean isHomeService() {
                return homeService != null && homeService == 1;
        }

        public void setHomeService(boolean v) {
                this.homeService = v ? 1 : 0;
        }

        public boolean isActive() {
                return active != null && active == 1;
        }

        public void setActive(boolean v) {
                this.active = v ? 1 : 0;
        }
}