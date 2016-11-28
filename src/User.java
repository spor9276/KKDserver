
public class User {
	private String id;
	private String password;
	private int ready_flag;
	private int master_flag;
	
	public User(String id, String password){
		this.id = id;
		this.password = password;
		ready_flag = 0;
		master_flag = 0;
	}
	
	public String getId(){
		return id;
	}
	public String getPassword(){
		return password;
	}

	public int getReady_flag() {
		return ready_flag;
	}

	public void setReady_flag(int ready_flag) {
		this.ready_flag = ready_flag;
	}

	public int getMaster_flag() {
		return master_flag;
	}

	public void setMaster_flag(int master_flag) {
		this.master_flag = master_flag;
	}
	
	
	
}
