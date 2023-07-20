package dev.alnat.simpleratelimiter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "client_setting")
public class ClientSetting {

    @Id
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(updatable = false, nullable = false)
    private String apiKey;

    @Column(updatable = false, nullable = false)
    private Integer rpm;

}
