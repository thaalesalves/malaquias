package es.thalesalv.chatrpg.adapters.data.db.entity;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lorebook_entry_regex")
public class LorebookRegexEntity {

    @Id
    @GeneratedValue(generator = "nanoid-generator")
    @GenericGenerator(name = "nanoid-generator", strategy = "es.thalesalv.chatrpg.application.util.NanoIdIdentifierGenerator")
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "regex")
    private String regex;

    @OneToOne
    @JoinColumn(name = "entry_id", referencedColumnName = "id")
    private LorebookEntryEntity lorebookEntry;

    @ManyToOne
    @JoinColumn(name = "lorebook_id", referencedColumnName = "id")
    private LorebookEntity lorebook;
}
