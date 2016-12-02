package app.entities;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@Table(name="build_part")
public class BuildPart implements Serializable {

	public BuildPart(Build build, Part part) {
		this.build = build;
		this.part = part;
	}
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column
	private Long id;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="build_id")
	private Build build;
	
	@ManyToOne
	@JoinColumn(name="part_id")
	private Part part;
	
	@Column
	private int quantity = 1;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column
	@JsonIgnore
	private Calendar createdAt = Calendar.getInstance();
	
	public BuildPart() {
	}

	public Build getBuild() {
		return build;
	}

	public void setBuild(Build build) {
		this.build = build;
	}

	public Part getPart() {
		return part;
	}

	public void setPart(Part part) {
		this.part = part;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Calendar getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Calendar createdAt) {
		this.createdAt = createdAt;
	}
}
