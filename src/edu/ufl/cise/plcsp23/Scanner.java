package edu.ufl.cise.plcsp23;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.ufl.cise.plcsp23.IToken.Kind;


class Scanner implements IScanner {
  private final String input;
  private final char[] inputChars;
  private int pos;
  private char ch;
  private int column;
  private int line;
  
  private static final Map<String, Kind> labels;
  static {
	  
	    labels = new HashMap<>();
	    labels.put("ident", Kind.IDENT);
	    labels.put("num_lit", Kind.NUM_LIT);
	    labels.put("string_lit", Kind.STRING_LIT);
	    labels.put("res_image", Kind.RES_image);
	    labels.put("res_pixel", Kind.RES_pixel);
	    labels.put("res_int", Kind.RES_int);
	    labels.put("res_string", Kind.RES_string);
	    labels.put("res_void", Kind.RES_void);
	    labels.put("res_nil", Kind.RES_nil);
	    labels.put("res_load", Kind.RES_load);
	    labels.put("res_display", Kind.RES_display);
	    labels.put("res_write", Kind.RES_write);
	    labels.put("res_x", Kind.RES_x);
	    labels.put("res_y", Kind.RES_y);
	    labels.put("res_a", Kind.RES_a);
	    labels.put("res_r", Kind.RES_r);
	    labels.put("res_X", Kind.RES_X);
	    labels.put("res_Y", Kind.RES_Y);
	    labels.put("res_Z", Kind.RES_Z);
	    labels.put("res_x_cart", Kind.RES_x_cart);
	    labels.put("res_y_cart", Kind.RES_y_cart);
	    labels.put("res_a_polar", Kind.RES_a_polar);
	    labels.put("res_r_polar", Kind.RES_r_polar);
	    labels.put("res_rand", Kind.RES_rand);
	    labels.put("res_sin", Kind.RES_sin);
	    labels.put("res_cos", Kind.RES_cos);
	    labels.put("res_atan", Kind.RES_atan);
	    labels.put("res_if", Kind.RES_if);
	    labels.put("res_while", Kind.RES_while);   
	    
	  }
  
  
  
  private enum State {START, AMP, LINE, IN_IDENT, HAVE_ZERO, HAVE_DOT, HAVE_ASTE,  
	   IN_NUM, IN_STR, HAVE_EQ, HAVE_LROW, HAVE_RROW, HAVE_EX, IN_COND, END_COND}
  
  //Constructor refrenced from 1/23 Slides
  public Scanner(String input) {
	  this.input = input;
	  inputChars = Arrays.copyOf(input.toCharArray(),input.length()+1);
	  pos = -1;
	  ch = inputChars[0];
	  column = 0;
	  line = 0;
  }



  @Override
  public IToken next() throws LexicalException {
	  return scanTokens();
  }
  
  private void nextChar() {
	  ++pos;
	  ch = inputChars[pos];
  }
  // Utility functions referenced from 1/23 Lecture Slides
  private boolean isDigit(int ch) {
	   return '0' <= ch && ch <= '9';
  }
  private boolean isLetter(int ch) {
	return ('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z');
  }
  private boolean isIdentStart(int ch) {
	 return isLetter(ch) || (ch == '$') || (ch == '_');
  }
  private void error(String message) throws LexicalException{
	 throw new LexicalException("Error at pos " + pos + ": " + message); 
  }

  
  private IToken scanTokens() throws LexicalException {
	  State state = State.START;
	  int tokenStart = -1;
	  while (true) {
		 nextChar();
		 switch (state) {
		 	case START -> {
		 		tokenStart = pos;
	 			++column;
		 		switch(ch) {
			 		case 0 -> { //end of input
			 		    return new Token(Kind.EOF, tokenStart, 0, inputChars, line, column);
			 		}
		 			case ' ', '\t', '\r', 'f' -> {}
		 			case '\n' -> {
		 				++line;
		 				column = 0;
		 			}
		 			case '.' -> {
			 		    return new Token(Kind.DOT, tokenStart, 1, inputChars, line, column);
		 			}
		 			case '?' -> {
		 				return new Token(Kind.EOF, tokenStart, 1, inputChars, line, column);
		 			}
		 			case '+' -> {
		 				return new Token(Kind.PLUS, tokenStart, 1, inputChars, line, column);
		 			}
		 			case '(' -> {
		 				return new Token(Kind.LPAREN, tokenStart, 1, inputChars, line, column);
		 			}

		 			case ')' -> {
		 				return new Token(Kind.RPAREN, tokenStart, 1, inputChars, line, column);
		 			}
		 			case '[' -> {
		 				return new Token(Kind.LSQUARE, tokenStart, 1, inputChars, line, column);
		 			}
			    	case ']' -> {
			    		return new Token(Kind.RSQUARE, tokenStart, 1, inputChars, line, column);
		 			}
			    	case '/' -> {
			    		return new Token(Kind.DIV, tokenStart, 1, inputChars, line, column);
		 			}
			    	case '%' -> {
			    		return new Token(Kind.MOD, tokenStart, 1, inputChars, line, column);
		 			}
			    	case ':' -> {
			    		return new Token(Kind.COLON, tokenStart, 1, inputChars, line, column);
		 			}
			    	case ',' -> {
			    		return new Token(Kind.COMMA, tokenStart, 1, inputChars, line, column);
		 			}
		 			case '{' -> {
		 				return new Token(Kind.LCURLY, tokenStart, 1, inputChars, line, column);
		 			}
		 			case '}' -> {
		 				return new Token(Kind.RCURLY, tokenStart, 1, inputChars, line, column);
		 			}
		 			case '!' -> {
		 				return new Token(Kind.BANG, tokenStart, 1, inputChars, line, column);
		 			}
		 			case '-' -> {
		 				return new Token(Kind.MINUS, tokenStart, 1, inputChars, line, column);
		 			}
		 			case '*' -> {
			    		state = State.HAVE_ASTE;
			    	}
		 			case '&' -> {
			    		state = State.AMP;
			    	}
		 			case '|' -> {
			    		state = State.LINE;
			    	}
			    	case '>' -> {
			    		state = State.HAVE_RROW;
			    	}
			    	case '<' -> {
			    		state = State.HAVE_LROW;
			    	}
		 			case '"' -> {
		 				state = State.IN_STR;
		 			}
			    	case '=' -> {
			    		state= State.HAVE_EQ;
			    	}
			    	case '0' -> {
			    		state = State.HAVE_ZERO;
			    	}
			    	case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
			    		state = State.IN_NUM;
			    	}

			    	case 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			    		 'a', 'b', 'c', 'd', 'e', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			    		 '_' -> {
			    			 state = State.IN_IDENT;
			    	}	 
			    	default -> {error("Invalid Character");}
		 		}
		 		}
		 
		 	
		 	
		 	case HAVE_EQ -> {
		 		switch(ch) {
		 			case '=' -> {
		 				return new Token(Kind.EQ, tokenStart, 2, inputChars, line, column);
		 			}
		 			default -> {
		 				return new Token(Kind.ASSIGN, tokenStart, 1, inputChars, line, column);
		 			}

		 		}
		 	}
		 	case AMP -> {
		 		switch(ch) {
		 			case '&' -> {
		 				return new Token(Kind.AND, tokenStart, 1, inputChars, line, column);

		 			}
		 			default -> {
		 				return new Token(Kind.BITAND, tokenStart, 1, inputChars, line, column);

		 			}

		 		}
		 	}
		 	

		 }
	  }
  }
};