// Java Chatting Server


import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Server extends JFrame {
	private JPanel contentPane;
	private JTextField textField; // 사용할 PORT번호 입력
	private JButton Start; // 서버를 실행시킨 버튼
	JTextArea textArea; // 클라이언트 및 서버 메시지 출력
	
	private ServerSocket socket; //서버소켓
	private Socket soc; // 연결소켓 
	private int Port; // 포트번호
	private Vector vc = new Vector(); // 연결된 사용자를 저장할 벡터

	public static void main(String[] args)
	{	
			Server frame = new Server();
			frame.setVisible(true);			
	}

	public Server() {
		//UserSetting();
		init();
	}
	
	private void init() { // GUI를 구성하는 메소드		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 280, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane js = new JScrollPane();				

		textArea = new JTextArea();
		textArea.setColumns(20);
		textArea.setRows(5);
		js.setBounds(0, 0, 264, 254);
		contentPane.add(js);
		js.setViewportView(textArea);

		textField = new JTextField("30000");
		textField.setBounds(98, 264, 154, 37);
		contentPane.add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(12, 264, 98, 37);
		contentPane.add(lblNewLabel);
		Start = new JButton("서버 실행");
		
		Start.setBounds(0, 325, 264, 37);
		contentPane.add(Start);
		server_start();
		textArea.setEditable(false); // textArea를 사용자가 수정 못하게끔 막는다.	
	}
	
	
	private void server_start() {
		try {
			socket = new ServerSocket(30000); // 서버가 포트 여는부분
			Start.setText("서버실행중");
			Start.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
			textField.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
			
			if(socket!=null) // socket 이 정상적으로 열렸을때
			{
				Connection();
			}
			
		} catch (IOException e) {
			textArea.append("소켓이 이미 사용중입니다...\n");

		}

	}

	private void Connection() {
		Thread th = new Thread(new Runnable() { // 사용자 접속을 받을 스레드
			@Override
			public void run() {
				while (true) { // 사용자 접속을 계속해서 받기 위해 while문
					try {
						textArea.append("사용자 접속 대기중...\n");
						soc = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
						textArea.append("사용자 접속!!\n");
						
						// 아이디 비밀번호 체크하기
						
						UserInfo user = new UserInfo(soc, vc); // 연결된 소켓 정보는 금방 사라지므로, user 클래스 형태로 객체 생성
	                                // 매개변수로 현재 연결된 소켓과, 벡터를 담아둔다
						vc.add(user); // 해당 벡터에 사용자 객체를 추가
						user.start(); // 만든 객체의 스레드 실행
					} catch (IOException e) {
						textArea.append("!!!! accept 에러 발생... !!!!\n");
					} 
				}
			}
		});
		th.start();
	}

	class UserInfo extends Thread {
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;
		private Socket user_socket;
		private Vector user_vc;
		private String Nickname = "";
		private User[] users;
		
		public UserInfo(Socket soc, Vector vc) // 생성자메소드
		{
			// 매개변수로 넘어온 자료 저장
			this.user_socket = soc;
			this.user_vc = vc;
			UserSetting();
			User_network();
		}
		private void UserSetting(){
			users = new User[4];
			users[0] = new User("user1","1234");
			users[1] = new User("user2","qwer");
			users[2] = new User("user3","asdf");
			users[3] = new User("user4","zxcv");
		}
		public void User_network() {
			int[] array = new int[4];	//로그인 유저 배열 인덱스
		//	int array_count = 0;		//로그인 유저 수
			try {
				is = user_socket.getInputStream();
				dis = new DataInputStream(is);
				os = user_socket.getOutputStream();
				dos = new DataOutputStream(os);
				//Nickname = dis.readUTF(); // 사용자의 닉네임 받는부분
				byte[] b=new byte[128];
				dis.read(b);
				String msg = new String(b);
				msg = msg.trim();
				String temp = msg;
				System.out.println("msg = " + temp);
				
				StringTokenizer s = new StringTokenizer(temp);
				String Command = s.nextToken("$");
				//String Password = s.nextToken(",");
				String temp2;
				System.out.println(Command + Nickname);
				switch(Command){
				case "LOGIN":
					int j=-1;
					String Nickname = s.nextToken("$");
					
					for(int i = 0;i<4;i++){
						if(users[i].getId().equals(Nickname)){
							j = i;
							textArea.append(Protocol.LOGINSUCCESS + Nickname + "\n");
							textArea.setCaretPosition(textArea.getText().length());	
							temp2 = Protocol.LOGINSUCCESS;
							temp2 = temp2 + Nickname;
							//System.out.println(temp2);
							send_Message(temp2); // 연결된 사용자에게 정상접속을 알림
							//send_Message("Login Success");
							//user_socket.close();
						}
					}
					
					if(j == -2){
						textArea.append(Protocol.ALREADY_ACCESS+Nickname + "\n");
						textArea.setCaretPosition(textArea.getText().length());
						temp2 = Protocol.ALREADY_ACCESS;
						temp2 = temp2 + Nickname;
						send_Message(temp2);
						user_socket.close();
					}
					if(j == -1 ){
						//연결 끊기
					//	textArea.append("ID " + Nickname + " ID 오류\n");
						textArea.append(Protocol.IDERROR+Nickname + "\n");
						textArea.setCaretPosition(textArea.getText().length());
						temp2 = Protocol.IDERROR;
						temp2 = temp2 + Nickname;
						send_Message(temp2);
						user_socket.close();
					}
					break;
				
				case "CHAT":
					broad_cast(msg);
					//채팅
					break;
					
				
				}
				
			} catch (Exception e) {
				textArea.append("스트림 셋팅 에러\n");
				textArea.setCaretPosition(textArea.getText().length());
			}
		}

		public void InMessage(String str) {
			//textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
			textArea.append(str + "\n");
			textArea.setCaretPosition(textArea.getText().length());
			// 사용자 메세지 처리
			broad_cast(str);
		}

		public void broad_cast(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserInfo imsi = (UserInfo) user_vc.elementAt(i);
				imsi.send_Message(str);
			}
		}

		public void send_Message(String str) {
			try {
				//dos.writeUTF(str);
				byte[] bb;		
				bb = str.getBytes();
				dos.write(bb); //.writeUTF(str);
			} 
			catch (IOException e) {
				textArea.append("메시지 송신 에러 발생\n");	
				textArea.setCaretPosition(textArea.getText().length());
			}
		}

		public void run() // 스레드 정의
		{

			while (true) {
				try {
					
					// 사용자에게 받는 메세지
					byte[] b = new byte[128];
					dis.read(b);
					String msg = new String(b);
					msg = msg.trim();
					//String msg = dis.readUTF();
					InMessage(msg);
					
				} 
				catch (IOException e) 
				{
					
					try {
						dos.close();
						dis.close();
						user_socket.close();
						vc.removeElement( this ); // 에러가난 현재 객체를 벡터에서 지운다
						textArea.append(vc.size() +" : 현재 벡터에 담겨진 사용자 수\n");
						textArea.append("사용자 접속 끊어짐 자원 반납\n");
						textArea.setCaretPosition(textArea.getText().length());
						
						break;
					
					} catch (Exception ee) {
					
					}// catch문 끝
				}// 바깥 catch문끝

			}
			
			
			
		}// run메소드 끝

	} // 내부 userinfo클래스끝

}
