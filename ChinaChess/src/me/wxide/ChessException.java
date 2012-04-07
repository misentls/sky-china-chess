package me.wxide;

public class ChessException extends Exception {
	
	private final static String NOSIDE="两家都不是的!";
	private final static String NULL_SPACE="有一个棋子是空的!";
	public final static String NETWORK_ERROR="网络连接异常！";
	public ChessException() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ChessException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	public ChessException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	public ChessException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	public static void noSide() throws ChessException{
		throw new ChessException(NOSIDE);
	}
	public static void networkError() throws ChessException{
		throw new ChessException(NETWORK_ERROR);
	}
	
}
