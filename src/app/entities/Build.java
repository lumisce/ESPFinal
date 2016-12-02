package app.entities;

import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonManagedReference;

import javax.persistence.JoinColumn;

@Entity
public class Build {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column
	private Long id;
	
	@Column
	private String name;
	
	@JsonIgnore
//	@JsonManagedReference
	@OneToMany(mappedBy="build")
	private List<BuildPart> buildParts;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@Transient
	private String username;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column
	@JsonIgnore
	private Calendar createdAt = Calendar.getInstance();
	
	@Transient
	private String created;
	
	@Transient
	private List<BuildPart> bParts;
	
	public Build() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<BuildPart> getBuildParts() {
		return buildParts;
	}

	public void setBuildParts(List<BuildPart> buildParts) {
		this.buildParts = buildParts;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Calendar getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Calendar createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public List<BuildPart> getbParts() {
		return bParts;
	}

	public void setbParts(List<BuildPart> bParts) {
		this.bParts = bParts;
	}
}
