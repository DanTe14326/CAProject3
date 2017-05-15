
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

/**
 * @author yanqing.qyq 2012-2015@USTC
 *         模板说明：该模板主要提供依赖Swing组件提供的JPanle，JFrame，JButton等提供的GUI。使用“监听器”
 *         模式监听各个Button的事件，从而根据具体事件执行不同方法。 Tomasulo算法核心需同学们自行完成，见说明（4）
 *         对于界面必须修改部分，见说明(1),(2),(3)
 *
 *         (1)说明：根据你的设计完善指令设置中的下拉框内容
 *         (2)说明：请根据你的设计指定各个面板（指令状态，保留站，Load部件，寄存器部件）的大小 (3)说明：设置界面默认指令 (4)说明：
 *         Tomasulo算法实现
 */

class Instruction {
	String name; // 指令名称
	String destination; // 目的操作数
	String source1; // 源操作数1
	String source2; // 源操作数2
}

class InstructionStation {
	String Qi; // 当前指令所在保留站名称
	int state; // 当前指令所处的状态
	int excutetime; // 指令执行所需要的时间
	Instruction instruction; // 该指令状态所对应的指令
}

class LoadStation {
	String Qi; // 保留站名称
	String Busy; // Load组件状态
	String Addr; // Load组件访存地址
	String value; // 访存值
}

class RegisterStation {
	String Qi; // 存放以该寄存器为目的寄存器的保留站名称
	String value; // 该寄存器的值
	String state; // 寄存器名称
}

class ReservationStation {
	String Op;// 指令操作的类型
	String Qi; // Qi为该保留站的名称，用以告知指定寄存器结果来源
	String Qj;// 产生指令第一个操作数值的保留站
	String Qk;// 产生指令第二个操作数值的保留站
	String Vj; // 指令第一个操作数：op2
	String Vk;// 指令第二个操作数：op3
	String Busy;// 保留站工作状态
}

public class Tomasulo extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * 界面上有六个面板： panel1 : 指令设置 panel2 : 执行时间设置 panel3 : 指令状态 panel4 : 保留站状态
	 * panel5 : Load部件 panel6 : 寄存器状态
	 */
	private JPanel panel1, panel2, panel3, panel4, panel5, panel6;

	/*
	 * 四个操作按钮：步进，进5步，重置，执行
	 */
	private JButton stepbut, step5but, resetbut, startbut;

	/*
	 * 指令选择框
	 */
	@SuppressWarnings("rawtypes")
	private JComboBox instbox[] = new JComboBox[24];

	/*
	 * 每个面板的名称
	 */
	private JLabel instl, timel, tl1, tl2, tl3, tl4, resl, regl, ldl, insl, stepsl;
	private int time[] = new int[4];

	/*
	 * 部件执行时间的输入框
	 */
	private JTextField tt1, tt2, tt3, tt4;

	private int intv[][] = new int[6][4], cnow;
	private int cal[][] = { { -1, 0, 0 }, { -1, 0, 0 }, { -1, 0, 0 }, { -1, 0, 0 }, { -1, 0, 0 } };
	private int ld[][] = { { 0, 0 }, { 0, 0 }, { 0, 0 } };
	private int ff[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	/*
	 * (1)说明：根据你的设计完善指令设置中的下拉框内容 inst：
	 * 指令下拉框内容:"NOP","L.D","ADD.D","SUB.D","MULT.D","DIV.D"………… inst：
	 * 目的寄存器下拉框内容:"F0","F2","F4","F6","F8" ………… fx：
	 * 源操作数寄存器内容:"R0","R1","R2","R3","R4","R5","R6","R7","R8","R9" ………… rx：
	 * 立即数下拉框内容:"0","1","2","3","4","5","6","7","8","9" …………ix
	 */
	public static int m = 0;
	private String inst[] = { "NOP", "L.D", "ADD.D", "SUB.D", "MULT.D", "DIV.D" },
			fx[] = { "F0", "F2", "F4", "F6", "F8", "F10", "F12", "F14", "F16", "F18", "F20", "F22", "F24", "F26", "F28",
					"F30" },
			rx[] = { "R0", "R1", "R2", "R3", "R4", "R5", "R6" },
			ix[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
					"18", "19", "20", "21", "22", "23", "24", "25" };

	/*
	 * (2)说明：请根据你的设计指定各个面板（指令状态，保留站，Load部件，寄存器部件）的大小 指令状态 面板 保留站 面板 Load部件 面板
	 * 寄存器 面板 的大小
	 */
	/*
	 * instst：指令状态列表(7行4列) resst：保留站列表(6行8列) ldst：load缓存列表(4行4列)
	 * regst：寄存器列表(3行17列)
	 */
	private String instst[][] = new String[7][4], resst[][] = new String[6][8], ldst[][] = new String[4][4],
			regst[][] = new String[3][17];
	private JLabel instjl[][] = new JLabel[7][4], resjl[][] = new JLabel[6][8], ldjl[][] = new JLabel[4][4],
			regjl[][] = new JLabel[3][17];

	// 初始化指令队列，指令状态集，保留站，Load缓存站，寄存器站
	private Instruction instruction[] = new Instruction[6];
	private InstructionStation IS[] = new InstructionStation[6];
	private ReservationStation RS[] = new ReservationStation[5];
	private LoadStation LS[] = new LoadStation[3];
	private RegisterStation RegS[] = new RegisterStation[16];

	// 构造方法
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Tomasulo() {
		super("Tomasulo Simulator");

		// 设置布局
		Container cp = getContentPane();
		FlowLayout layout = new FlowLayout();
		cp.setLayout(layout);

		// 指令设置。GridLayout(int 指令条数, int 操作码+操作数, int hgap, int vgap)
		instl = new JLabel("指令设置");
		panel1 = new JPanel(new GridLayout(6, 4, 0, 0));
		panel1.setPreferredSize(new Dimension(350, 150));
		panel1.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		// 操作按钮:执行，重设，步进，步进5步
		timel = new JLabel("执行时间设置");
		panel2 = new JPanel(new GridLayout(2, 4, 0, 0));
		panel2.setPreferredSize(new Dimension(280, 80));
		panel2.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		// 指令状态
		insl = new JLabel("指令状态");
		panel3 = new JPanel(new GridLayout(7, 4, 0, 0));
		panel3.setPreferredSize(new Dimension(420, 175));
		panel3.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		// 保留站
		resl = new JLabel("保留站");
		panel4 = new JPanel(new GridLayout(6, 7, 0, 0));
		panel4.setPreferredSize(new Dimension(420, 150));
		panel4.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		// Load部件
		ldl = new JLabel("Load部件");
		panel5 = new JPanel(new GridLayout(4, 4, 0, 0));
		panel5.setPreferredSize(new Dimension(300, 100));
		panel5.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		// 寄存器状态
		regl = new JLabel("寄存器");
		panel6 = new JPanel(new GridLayout(3, 17, 0, 0));
		panel6.setPreferredSize(new Dimension(740, 75));
		panel6.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		tl1 = new JLabel("Load");
		tl2 = new JLabel("加/减");
		tl3 = new JLabel("乘法");
		tl4 = new JLabel("除法");

		// 操作按钮:执行，重设，步进，步进5步
		stepsl = new JLabel();
		stepsl.setPreferredSize(new Dimension(200, 30));
		stepsl.setHorizontalAlignment(SwingConstants.CENTER);
		stepsl.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		stepbut = new JButton("步进");
		stepbut.addActionListener(this);
		step5but = new JButton("步进5步");
		step5but.addActionListener(this);
		startbut = new JButton("执行");
		startbut.addActionListener(this);
		resetbut = new JButton("重设");
		resetbut.addActionListener(this);
		/* 设置执行周期初始值 */
		tt1 = new JTextField("2");
		tt2 = new JTextField("2");
		tt3 = new JTextField("10");
		tt4 = new JTextField("40");

		// 指令设置
		/*
		 * 设置指令选择框（操作码，操作数，立即数等）的default选择
		 */
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 4; j++) {
				if (j == 0) {
					instbox[i * 4 + j] = new JComboBox(inst);
				} else if (j == 1) {
					instbox[i * 4 + j] = new JComboBox(fx);
				} else if (j == 2) {
					instbox[i * 4 + j] = new JComboBox(ix);
				} else {
					instbox[i * 4 + j] = new JComboBox(rx);
				}
				instbox[i * 4 + j].addActionListener(this);
				panel1.add(instbox[i * 4 + j]);
			}
		for (int i = 2; i < 6; i++)
			for (int j = 0; j < 4; j++) {
				if (j == 0) {
					instbox[i * 4 + j] = new JComboBox(inst);
				} else {
					instbox[i * 4 + j] = new JComboBox(fx);
				}
				instbox[i * 4 + j].addActionListener(this);
				panel1.add(instbox[i * 4 + j]);
			}
		/*
		 * (3)说明：设置界面默认指令，根据你设计的指令，操作数等的选择范围进行设置。 默认6条指令。待修改
		 */
		/* L.D F8,21(R3) */
		instbox[0].setSelectedIndex(1);
		instbox[1].setSelectedIndex(4);
		instbox[2].setSelectedIndex(21);
		instbox[3].setSelectedIndex(3);
		/* L.D F4,16(R4) */
		instbox[4].setSelectedIndex(1);
		instbox[5].setSelectedIndex(2);
		instbox[6].setSelectedIndex(16);
		instbox[7].setSelectedIndex(4);
		/* MUL.D F2,F4,F6 */
		instbox[8].setSelectedIndex(4);
		instbox[9].setSelectedIndex(1);
		instbox[10].setSelectedIndex(2);
		instbox[11].setSelectedIndex(3);
		/* SUB.D F10,F8,F4 */
		instbox[12].setSelectedIndex(3);
		instbox[13].setSelectedIndex(5);
		instbox[14].setSelectedIndex(4);
		instbox[15].setSelectedIndex(2);
		/* DIV.D F12,F2,F8 */
		instbox[16].setSelectedIndex(5);
		instbox[17].setSelectedIndex(6);
		instbox[18].setSelectedIndex(1);
		instbox[19].setSelectedIndex(4);
		/* ADD.D F8,F10,F4 */
		instbox[20].setSelectedIndex(2);
		instbox[21].setSelectedIndex(4);
		instbox[22].setSelectedIndex(5);
		instbox[23].setSelectedIndex(2);

		// 执行时间设置
		panel2.add(tl1);
		panel2.add(tt1);
		panel2.add(tl2);
		panel2.add(tt2);
		panel2.add(tl3);
		panel2.add(tt3);
		panel2.add(tl4);
		panel2.add(tt4);

		// 指令状态设置
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 4; j++) {
				instjl[i][j] = new JLabel(instst[i][j]);
				instjl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				panel3.add(instjl[i][j]);
			}
		}
		// 保留站设置
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 8; j++) {
				resjl[i][j] = new JLabel(resst[i][j]);
				resjl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				panel4.add(resjl[i][j]);
			}
		}
		// Load部件设置
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				ldjl[i][j] = new JLabel(ldst[i][j]);
				ldjl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				panel5.add(ldjl[i][j]);
			}
		}
		// 寄存器设置
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 17; j++) {
				regjl[i][j] = new JLabel(regst[i][j]);
				regjl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				panel6.add(regjl[i][j]);
			}
		}

		// 向容器添加以上部件
		cp.add(instl);
		cp.add(panel1);
		cp.add(timel);
		cp.add(panel2);

		cp.add(startbut);
		cp.add(resetbut);
		cp.add(stepbut);
		cp.add(step5but);

		cp.add(panel3);
		cp.add(insl);
		cp.add(panel5);
		cp.add(ldl);
		cp.add(panel4);
		cp.add(resl);
		cp.add(stepsl);
		cp.add(panel6);
		cp.add(regl);

		stepbut.setEnabled(false);
		step5but.setEnabled(false);
		panel3.setVisible(false);
		insl.setVisible(false);
		panel4.setVisible(false);
		ldl.setVisible(false);
		panel5.setVisible(false);
		resl.setVisible(false);
		stepsl.setVisible(false);
		panel6.setVisible(false);
		regl.setVisible(false);
		setSize(820, 660);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/*
	 * 点击”执行“按钮后，根据选择的指令，初始化其他几个面板
	 */
	public void init() {
		// get value
		/* intv：6行4列的整型数组 */
		for (int i = 0; i < 6; i++) {
			intv[i][0] = instbox[i * 4].getSelectedIndex();
			if (intv[i][0] != 0) {
				intv[i][1] = 2 * instbox[i * 4 + 1].getSelectedIndex();
				/* 指令形式为load时，选择列表为fx,ix,rx */
				if (intv[i][0] == 1) {
					intv[i][2] = instbox[i * 4 + 2].getSelectedIndex();
					intv[i][3] = instbox[i * 4 + 3].getSelectedIndex();
				}
				/* 指令形式为算术运算指令时，选择列表为fx,fx,fx */
				else {
					intv[i][2] = 2 * instbox[i * 4 + 2].getSelectedIndex();
					intv[i][3] = 2 * instbox[i * 4 + 3].getSelectedIndex();
				}
			}
		}
		/*
		 * 获取文本框中字符串 为指令执行周期
		 */
		time[0] = Integer.parseInt(tt1.getText());
		time[1] = Integer.parseInt(tt2.getText());
		time[2] = Integer.parseInt(tt3.getText());
		time[3] = Integer.parseInt(tt4.getText());
		// System.out.println(time[0]);
		// set 0
		instst[0][0] = "指令";
		instst[0][1] = "发射";
		instst[0][2] = "执行";
		instst[0][3] = "写回";

		ldst[0][0] = "名称";
		ldst[0][1] = "Busy";
		ldst[0][2] = "地址";
		ldst[0][3] = "值";
		ldst[1][0] = "Load1";
		ldst[2][0] = "Load2";
		ldst[3][0] = "Load3";
		ldst[1][1] = "no";
		ldst[2][1] = "no";
		ldst[3][1] = "no";

		resst[0][0] = "Time";
		resst[0][1] = "名称";
		resst[0][2] = "Busy";
		resst[0][3] = "Op";
		resst[0][4] = "Vj";
		resst[0][5] = "Vk";
		resst[0][6] = "Qj";
		resst[0][7] = "Qk";
		resst[1][1] = "Add1";
		resst[2][1] = "Add2";
		resst[3][1] = "Add3";
		resst[4][1] = "Mult1";
		resst[5][1] = "Mult2";
		resst[1][2] = "no";
		resst[2][2] = "no";
		resst[3][2] = "no";
		resst[4][2] = "no";
		resst[5][2] = "no";

		regst[0][0] = "字段";
		for (int i = 1; i < 17; i++) {
			// System.out.print(i+" "+fx[i-1];
			regst[0][i] = fx[i - 1];

		}
		regst[1][0] = "状态";
		regst[2][0] = "值";

		for (int i = 1; i < 7; i++) {
			instruction[i - 1] = new Instruction();
			for (int j = 0; j < 4; j++) {
				if (j == 0) {
					int temp = i - 1;
					String disp; // 获取指令类型
					disp = inst[instbox[temp * 4].getSelectedIndex()] + " ";
					if (instbox[temp * 4].getSelectedIndex() == 0) {
						// NOP指令的情况
						// disp = disp;
						// 每条指令所对应的指令名称、目的寄存器、源操作数1、源操作数2。对应instbox指定行4个元素
						instruction[i - 1].name = inst[instbox[temp * 4].getSelectedIndex()];
						instruction[i - 1].destination = fx[instbox[temp * 4 + 1].getSelectedIndex()];
						instruction[i - 1].source1 = fx[instbox[temp * 4 + 2].getSelectedIndex()];
						instruction[i - 1].source2 = fx[instbox[temp * 4 + 3].getSelectedIndex()];
					} else if (instbox[temp * 4].getSelectedIndex() == 1) {
						// load指令的情况
						disp = disp + fx[instbox[temp * 4 + 1].getSelectedIndex()] + ','
								+ ix[instbox[temp * 4 + 2].getSelectedIndex()] + '('
								+ rx[instbox[temp * 4 + 3].getSelectedIndex()] + ')';
						// 每条指令所对应的指令名称、目的寄存器、源操作数1、源操作数2。对应instbox指定行4个元素
						instruction[i - 1].name = inst[instbox[temp * 4].getSelectedIndex()];
						instruction[i - 1].destination = fx[instbox[temp * 4 + 1].getSelectedIndex()];
						instruction[i - 1].source1 = ix[instbox[temp * 4 + 2].getSelectedIndex()];
						instruction[i - 1].source2 = rx[instbox[temp * 4 + 3].getSelectedIndex()];
					} else {
						// 运算指令的情况
						disp = disp + fx[instbox[temp * 4 + 1].getSelectedIndex()] + ','
								+ fx[instbox[temp * 4 + 2].getSelectedIndex()] + ','
								+ fx[instbox[temp * 4 + 3].getSelectedIndex()];
						// 每条指令所对应的指令名称、目的寄存器、源操作数1、源操作数2。对应instbox指定行4个元素
						instruction[i - 1].name = inst[instbox[temp * 4].getSelectedIndex()];
						instruction[i - 1].destination = fx[instbox[temp * 4 + 1].getSelectedIndex()];
						instruction[i - 1].source1 = fx[instbox[temp * 4 + 2].getSelectedIndex()];
						instruction[i - 1].source2 = fx[instbox[temp * 4 + 3].getSelectedIndex()];
					}
					instst[i][j] = disp;
				} else
					instst[i][j] = "";

			}
			// 指令状态列表初始化
			IS[i - 1] = new InstructionStation();
			IS[i - 1].state = 0;
			IS[i - 1].instruction = instruction[i - 1];
			IS[i - 1].excutetime = getTimeForEX(instruction[i - 1]);
		}

		for (int i = 1; i < 6; i++)
			for (int j = 0; j < 8; j++)
				if (j != 1 && j != 2) {
					resst[i][j] = "";
				}
		for (int i = 1; i < 4; i++)
			for (int j = 2; j < 4; j++) {
				ldst[i][j] = "";
			}
		for (int i = 1; i < 3; i++)
			for (int j = 1; j < 17; j++) {
				regst[i][j] = "";
			}
		for (int i = 0; i < 5; i++) {
			for (int j = 1; j < 3; j++)
				cal[i][j] = 0;
			cal[i][0] = -1;
		}
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 2; j++)
				ld[i][j] = 0;
		for (int i = 0; i < 17; i++)
			ff[i] = 0;

		// 保留站，寄存器站，Load缓存站获取面板初始值
		for (int i = 0; i < 5; i++) {
			RS[i] = new ReservationStation();
			RS[i].Qi = resst[i + 1][1];
			RS[i].Busy = resst[i + 1][2];
			RS[i].Op = resst[i + 1][3];
			RS[i].Vj = resst[i + 1][4];
			RS[i].Vk = resst[i + 1][5];
			RS[i].Qj = resst[i + 1][6];
			RS[i].Qk = resst[i + 1][7];
			// System.out.print(RS[i].Qi);
		}
		for (int i = 0; i < 3; i++) {
			LS[i] = new LoadStation();
			LS[i].Qi = ldst[i + 1][0];
			LS[i].Busy = ldst[i + 1][1];
			LS[i].Addr = ldst[i + 1][2];
			LS[i].value = ldst[i + 1][3];
		}
		for (int i = 0; i < 16; i++) {
			RegS[i] = new RegisterStation();
			RegS[i].state = regst[0][i + 1];
			RegS[i].Qi = regst[1][i + 1];
			RegS[i].value = regst[2][i + 1];
		}
	}

	/*
	 * 点击操作按钮后，用于显示结果
	 */
	public void display() {
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 4; j++) {
				instjl[i][j].setText(instst[i][j]);
			}
		}
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 8; j++) {
				resjl[i][j].setText(resst[i][j]);
			}
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				ldjl[i][j].setText(ldst[i][j]);
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 17; j++) {
				regjl[i][j].setText(regst[i][j]);
			}
		}
		stepsl.setText("当前周期：" + String.valueOf(cnow - 1));
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		// 点击“执行”按钮的监听器
		if (e.getSource() == startbut) {
			for (int i = 0; i < 24; i++) {
				instbox[i].setEnabled(false);
			}
			tt1.setEnabled(false);
			tt2.setEnabled(false);
			tt3.setEnabled(false);
			tt4.setEnabled(false);
			stepbut.setEnabled(true);
			step5but.setEnabled(true);
			startbut.setEnabled(false);
			// 根据指令设置的指令初始化其他的面板
			init();
			cnow = 1;
			// 展示其他面板
			display();
			panel3.setVisible(true);
			panel4.setVisible(true);
			panel5.setVisible(true);
			panel6.setVisible(true);
			insl.setVisible(true);
			ldl.setVisible(true);
			resl.setVisible(true);
			stepsl.setVisible(true);
			regl.setVisible(true);
			core();
			cnow++;
			display();
		}
		// 点击“重置”按钮的监听器
		if (e.getSource() == resetbut) {
			m = 0;
			for (int i = 0; i < 24; i++) {
				instbox[i].setEnabled(true);
			}
			tt1.setEnabled(true);
			tt2.setEnabled(true);
			tt3.setEnabled(true);
			tt4.setEnabled(true);
			stepbut.setEnabled(false);
			step5but.setEnabled(false);
			startbut.setEnabled(true);
			panel3.setVisible(false);
			insl.setVisible(false);
			panel4.setVisible(false);
			ldl.setVisible(false);
			panel5.setVisible(false);
			resl.setVisible(false);
			stepsl.setVisible(false);
			panel6.setVisible(false);
			regl.setVisible(false);

			init();
			cnow = 1;
		}
		// 点击“步进”按钮的监听器
		if (e.getSource() == stepbut) {
			core();
			cnow++;
			display();
		}
		// 点击“进5步”按钮的监听器
		if (e.getSource() == step5but) {
			for (int i = 0; i < 5; i++) {
				core();
				cnow++;
			}
			display();
		}

		for (int i = 0; i < 24; i = i + 4) {
			if (e.getSource() == instbox[i]) {
				if (instbox[i].getSelectedIndex() == 1) {
					instbox[i + 2].removeAllItems();
					for (int j = 0; j < ix.length; j++)
						instbox[i + 2].addItem(ix[j]);
					instbox[i + 3].removeAllItems();
					for (int j = 0; j < rx.length; j++)
						instbox[i + 3].addItem(rx[j]);
				} else {
					instbox[i + 2].removeAllItems();
					for (int j = 0; j < fx.length; j++)
						instbox[i + 2].addItem(fx[j]);
					instbox[i + 3].removeAllItems();
					for (int j = 0; j < fx.length; j++)
						instbox[i + 3].addItem(fx[j]);
				}
			}
		}
	}
	/*
	 * (4)说明： Tomasulo算法实现
	 */

	public int getTimeForEX(Instruction instruction) {	//指令对应的cycle
		if (instruction.name == "L.D") {
			return Integer.parseInt(tt1.getText());
		} else if (instruction.name == "ADD.D" || instruction.name == "SUB.D") {
			return Integer.parseInt(tt2.getText());
		} else if (instruction.name == "MULT.D") {
			return Integer.parseInt(tt3.getText());
		} else if (instruction.name == "DIV.D") {
			return Integer.parseInt(tt4.getText());
		} else {
			return 0;
		}
	}

	
	public void core() {		//核心算法
		int numIssue, numEx1[], numEx2[], numWB[];
		numIssue = this.IS_getstate(IS);//等待发射的指令编号
		numEx1 = this.EX1_getstate(IS);//等待执行的指令编号
		numEx2 = this.EX2_getstate(IS);//正在执行还未执行完的指令编号
		numWB = this.WB_getstate(IS);//等待写回的指令编号
		
		// 发射指令：state置1(执行条件为原值等于0，即指令队列中的等待状态)
		if (numIssue != -1) {
			InstructionStation insIS = IS[numIssue];//待发射指令
			if (insIS.instruction.name == "L.D") {
				// 当前指令为Load指令时
				int numIDLoad = this.IDLE_load(LS);
				if (numIDLoad != -1) {//有空的load缓存部件
					insIS.Qi = LS[numIDLoad].Qi;//记录当前指令所在保留站的名称
					LS[numIDLoad].Busy = "yes";
					ldst[numIDLoad + 1][1] = LS[numIDLoad].Busy;
					LS[numIDLoad].value = insIS.instruction.source1;
					ldst[numIDLoad + 1][3] = LS[numIDLoad].value;
				}
			} else {
				// 当前指令为运算指令时
				int numIDRes = this.IDLE_resvstation(IS[numIssue], RS);//获取空闲保留站计算部件编号
				if (numIDRes != -1) {
					insIS.Qi = RS[numIDRes].Qi;
					RS[numIDRes].Busy = "yes";
					resst[numIDRes + 1][2] = RS[numIDRes].Busy;
					RS[numIDRes].Op = insIS.instruction.name;
					resst[numIDRes + 1][3] = RS[numIDRes].Op;
					// 循环查询已发射指令，如其有目的寄存器作为当前指令的源操作数来源时进行处理
					boolean opj = false, opk = false;
					for (int i = 0; i < numIssue; i++) {
						String destination = IS[i].instruction.destination;
						// 源操作第一寄存器：Qj,Qk
						if (insIS.instruction.source1 == destination) {
							opj = true;
							for (int j = 0; j < RegS.length; j++) {
								if (RegS[j].state == destination) {
									if (RegS[j].value == "") {
										RS[numIDRes].Qj = RegS[j].Qi;
										resst[numIDRes + 1][6] = RS[numIDRes].Qj;
									} else {
										RS[numIDRes].Vj = RegS[j].value;
										resst[numIDRes + 1][4] = RS[numIDRes].Vj;
									}
								}
							}
						}
						// 源操作第二寄存器：Vj,Vk
						if (insIS.instruction.source2 == destination) {
							opk = true;
							for (int j = 0; j < RegS.length; j++) {
								if (RegS[j].state == destination) {
									if (RegS[j].value == "") {
										RS[numIDRes].Qk = RegS[j].Qi;
										resst[numIDRes + 1][7] = RS[numIDRes].Qk;
									} else {
										RS[numIDRes].Vk = RegS[j].value;
										resst[numIDRes + 1][5] = RS[numIDRes].Vk;
									}
								}
							}
						}
					}
					
					// 若无寄存器相关，则直接对保留站操作数进行赋值
					if (!opj) {
						RS[numIDRes].Vj = insIS.instruction.source1;
						resst[numIDRes + 1][4] = "R[" + RS[numIDRes].Vj + "]";
					}
					if (!opk) {
						RS[numIDRes].Vk = insIS.instruction.source2;
						resst[numIDRes + 1][5] = "R[" + RS[numIDRes].Vk + "]";
					}
				}

			}
			
			// 寄存器站对该发射指令做出响应
			String destination = insIS.instruction.destination;
			String Qi = insIS.Qi;
			for (int i = 0; i < this.RegS.length; i++) {
				if (RegS[i].state == destination) {
					RegS[i].Qi = Qi;
					regst[1][i + 1] = RegS[i].Qi;
					break;
				}
			}
			// 修改该指令状态
			instst[numIssue + 1][1] = String.valueOf(cnow);
			IS[numIssue].state = 1;
		}
		

		// 指令进入执行阶段：state值为1
		for (int i = 0; i < numEx1.length; i++) {
			if (numEx1[i] != -1) {
				InstructionStation insISex1 = IS[numEx1[i]];
				if (insISex1.instruction.name == "L.D") {
					// 当为load指令时，更新地址栏，更新指令状态集中的执行列表
					for (int j = 0; j < LS.length; j++) {
						if (LS[j].Qi == insISex1.Qi) {
							LS[j].Addr = "R[" + insISex1.instruction.source2 + "]" + insISex1.instruction.source1;//计算访存地址
							ldst[j + 1][2] = LS[j].Addr;
							insISex1.excutetime--;
							break;
						}
					}
					if (insISex1.excutetime > 0) {
						instst[numEx1[i] + 1][2] = String.valueOf(cnow) + "→";//在面板上显示执行过程
						IS[numEx1[i]].state = 2;
					} else if (insISex1.excutetime == 0) {
						instst[numEx1[i] + 1][2] = String.valueOf(cnow);//获取当前的时钟周期
						IS[numEx1[i]].state = 3;
					}
				} else {
					for (int j = 0; j < RS.length; j++) {
						if (RS[j].Qi == insISex1.Qi) {
							if (!RS[j].Vj.equals("") && !RS[j].Vk.equals("")) {
								insISex1.excutetime--;
								resst[j + 1][0] = String.valueOf(insISex1.excutetime);
								if (insISex1.excutetime > 0) {
									instst[numEx1[i] + 1][2] = String.valueOf(cnow) + "→";
									IS[numEx1[i]].state = 2;
									break;
								} else if (insISex1.excutetime == 0) {
									instst[numEx1[i] + 1][2] = String.valueOf(cnow);
									IS[numEx1[i]].state = 3;
									break;
								}
							}
						}
					}
				}
			}
		}
		
		
		// 指令从执行到结束阶段，更新指令执行时间数
		for (int i = 0; i < numEx2.length; i++) {
			if (numEx2[i] != -1) {
				InstructionStation insISex2 = IS[numEx2[i]];
				if (insISex2.instruction.name == "L.D") {
					// 当指令为load指令时，更新load缓存中的值
					for (int j = 0; j < LS.length; j++) {
						if (LS[j].Qi == insISex2.Qi) {
							LS[j].value = "M[" + LS[j].Addr + "]";
							ldst[j + 1][3] = LS[j].value;
							insISex2.excutetime--;
							break;
						}
					}
					if (insISex2.excutetime == 0) {
						instst[numEx2[i] + 1][2] += String.valueOf(cnow);
						IS[numEx2[i]].state = 3;
					}
				} else {
					// 如果为其他运算指令,更新保留站中的执行计时时间
					int j;
					for (j = 0; j < RS.length; j++) {
						if (RS[j].Qi == insISex2.Qi) {
							insISex2.excutetime--;
							resst[j + 1][0] = String.valueOf(insISex2.excutetime);
							break;
						}
					}
					if (insISex2.excutetime == 0) {
						instst[numEx2[i] + 1][2] += String.valueOf(cnow);
						IS[numEx2[i]].state = 3;
						resst[j + 1][0] = "";
					}
				}
			}
		}

		
		// 执行完毕，写回过程
		for (int i = 0; i < numWB.length; i++) {
			if (numWB[i] != -1) {
				InstructionStation instrnswb = IS[numWB[i]];
				String Qi4 = instrnswb.Qi;
				if (instrnswb.instruction.name == "L.D") {
					// 指令为load指令时，写回，取消对load缓存站相应站位的占用
					for (int j = 0; j < LS.length; j++) {
						if (LS[j].Qi == instrnswb.Qi) {
							ldst[j + 1][1] = LS[j].Busy = "no";
							ldst[j + 1][2] = LS[j].Addr = "";
							ldst[j + 1][3] = LS[j].value = "";
							break;
						}
					}
				} else {
					// 指令为其他指令时更新保留站的信息，解除相应保留站的占用]
					for (int j = 0; j < RS.length; j++) {
						if (RS[j].Qi == Qi4) {
							RS[j].Busy = "no";
							RS[j].Op = "";
							RS[j].Qj = "";
							RS[j].Qk = "";
							RS[j].Vj = "";
							RS[j].Vk = "";
							resst[j + 1][2] = RS[j].Busy;
							for (int k = 3; k < 8; k++)
								resst[j + 1][k] = "";
							break;
						}
					}
				}
				// 更新指令目的寄存器对应的寄存器站
				for (int j = 0; j < RegS.length; j++) {
					if (RegS[j].Qi == Qi4) {
						m++;
						regst[2][j + 1] = RegS[j].value = "M" + m;
					}
				}
				// 更新保留站中需要该寄存器值的源操作数
				for (int j = 0; j < RS.length; j++) {
					if (RS[j].Qj == Qi4) {
						resst[j + 1][4] = RS[j].Vj = "M" + m;
						resst[j + 1][6] = RS[j].Qj = "";
						continue;
					}
					if (RS[j].Qk == Qi4) {
						resst[j + 1][5] = RS[j].Vk = "M" + m;
						resst[j + 1][7] = RS[j].Qk = "";
					}
				}
				instst[numWB[i] + 1][3] = String.valueOf(cnow);
				IS[numWB[i]].state = 4;
			}
		}

		boolean completed = true;
		for (int l = 0; l < IS.length; l++) {
			if (IS[l].instruction.name != "NOP" && instst[l + 1][3] == "") {
				completed = false;
				break;
			}
		}
		if (completed == true) {
			stepbut.setEnabled(false);
			step5but.setEnabled(false);
		}

	}

	// 获取空闲load缓存部件编号
	private int IDLE_load(LoadStation LS[]) {
		for (int i = 0; i < LS.length; i++) {
			if (LS[i].Busy == "no") {
				return i;
			}
		}
		return -1;
	}

	// 获取空闲保留站计算部件(Add或Mult)编号
	private int IDLE_resvstation(InstructionStation IS, ReservationStation RS[]) {
		if (IS.instruction.name == "ADD.D" || IS.instruction.name == "SUB.D") {
			for (int i = 0; i < 3; i++) {
				if (RS[i].Busy == "no") {
					return i;
				}
			}
		} else if (IS.instruction.name == "MULT.D" || IS.instruction.name == "DIV.D") {
			for (int i = 3; i < 5; i++) {
				if (RS[i].Busy == "no") {
					return i;
				}
			}
		}
		return -1;
	}

	// 返回等待执行写回的指令编号(发射序排列)
	private int[] WB_getstate(InstructionStation IS[]) {
		int n = 0;
		for (int i = 0; i < IS.length; i++) {
			if (IS[i].state == 3 && IS[i].Qi != "NOP") {
				n++;
			}
		}
		int num_wb[] = new int[n];
		for (int i = 0; i < n; i++) {
			num_wb[i] = -1;
		}
		// 找出执行完毕却没有写回的指令，返回其在指令队列中的位置
		for (int i = 0, j = 0; i < IS.length; i++) {
			if (IS[i].state == 3 && IS[i].Qi != "NOP") {
				num_wb[j] = i;
				j++;
			}
		}
		return num_wb;
	}

	// 返回正在执行还未执行完毕的指令编号
	private int[] EX2_getstate(InstructionStation IS[]) {
		int n = 0;
		for (int i = 0; i < IS.length; i++) {
			if (IS[i].state == 2 && IS[i].Qi != "NOP") {
				n++;
			}
		}
		int num_ex2[] = new int[n];
		for (int i = 0; i < n; i++) {
			num_ex2[i] = -1;
		}
		// 找出正在执行却没有执行完毕的指令，返回其在指令队列中的位置
		for (int i = 0, j = 0; i < IS.length; i++) {
			if (IS[i].state == 2 && IS[i].Qi != "NOP") {
				num_ex2[j] = i;
				j++;
			}
		}
		return num_ex2;
	}

	// 返回等待执行的指令编号
	private int[] EX1_getstate(InstructionStation IS[]) {
		int n = 0;
		for (int i = 0; i < IS.length; i++) {
			if (IS[i].state == 1 && IS[i].Qi != "NOP") {
				n++;
			}
		}
		int num_ex1[] = new int[n];
		for (int i = 0; i < n; i++) {
			num_ex1[i] = -1;
		}
		// 找出已发射但还在等待执行的指令，返回其在指令队列中的位置
		for (int i = 0, j = 0; i < IS.length; i++) {
			if (IS[i].state == 1 && IS[i].Qi != "NOP") {
				num_ex1[j] = i;
				j++;
			}
		}
		return num_ex1;
	}

	// 返回等待发射的指令编号
	private int IS_getstate(InstructionStation IS[]) {
		int num_issue = -1;
		for (int i = 0; i < IS.length; i++) {
			if (IS[i].state == 0 && IS[i].Qi != "NOP") {
				num_issue = i;
				break;
			}
		}
		return num_issue;
	}

	public static void main(String[] args) {
		new Tomasulo();
	}

}

