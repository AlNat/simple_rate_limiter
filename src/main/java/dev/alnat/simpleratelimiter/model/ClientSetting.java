package dev.alnat.simpleratelimiter.model;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(updatable = false, nullable = false)
    private String apiKey;

    @Column(updatable = false, nullable = false)
    private Integer rpm;


    public ClientSetting(String apiKey, Integer rpm) {
        this.apiKey = apiKey;
        this.rpm = rpm;
    }

    public static ClientSetting of(String apiKey, Integer rpm) {
       return new ClientSetting(apiKey, rpm);
    }

}
