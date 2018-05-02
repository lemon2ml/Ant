package cn.edu.swust.taskscheduing.asMaxmin;

import java.io.IOException;
import java.util.Vector;

import cn.edu.swust.taskschecuing.ant.Ant;
import cn.edu.swust.taskscheduing.ExpData;
import cn.edu.swust.taskscheduing.ExpDataTool;
import cn.edu.swust.taskscheduingmethed.MCT;

 
/**
 * ������Դ״̬
 * ��Ӣ��Ⱥ�㷨
 *  
 */
public class ACO {
	
	private Ant[] ants; // ����
	private int antNum; // ��������
	private int taskNum; // ��������
	private int vmNum; //���������
	private int MAX_GEN; // ���д���
	private float[][] pheromone; // ��Ϣ�ؾ���
	private Float[][] etc; // �������
	private float bestCost; // ��ѳ���
	Vector<MCT> seqExcute; // ���·��
	
	// ��������
	private float alpha;
	private float beta;
	private float rho;
	private float t0=0.000001f;
	private float minP;
	private float maxP;
	private float Q=1.0f;
	public ACO() {

	}

	/**
	 * 
	 * @param taskn
	 * @param vmNum
	 * @param antn
	 * @param MAX_GEN
	 * @param a
	 * @param b
	 * @param r
	 */
	public ACO(int taskn,int vmNum, int antn, int MAX_GEN, float a, float b, float r) {
		this.taskNum = taskn;
		this.vmNum=vmNum;
		this.antNum = antn;
		this.ants = new Ant[antNum];
		this.MAX_GEN = MAX_GEN;
		this.alpha = a;
		this.beta = b;
		this.rho = r;

	}

	public Vector<MCT> getSeqExcute() {
		return seqExcute;
	}

	public void setSeqExcute(Vector<MCT> seqExcute) {
		this.seqExcute = seqExcute;
	}

	/**
	 * ��ʼ��ACO�㷨��
	 * @param filename �����ļ��������ļ��洢���г��нڵ���������
	 * @throws IOException
	 */
	public void init(ExpData expData) throws IOException { 

		etc = expData.etc;

		// ��ʼ����Ϣ�ؾ���
		pheromone = new float[taskNum][vmNum];
		for (int i = 0; i < taskNum; i++) {
			for (int j = 0; j < vmNum; j++) {
				pheromone[i][j] = t0; // ��ʼ��Ϊ0.001
			}
		}
		minP=t0/(1024*1024);
		maxP=t0*256;
		bestCost = Float.MAX_VALUE;
		
		for (int i = 0; i < antNum; i++) {
			ants[i] = new Ant(taskNum,vmNum); 
		}
	}

	public void solve() {

		for (int g = 0; g < MAX_GEN; g++) { 
			//1.һ�����ϣ���ֻ����Ѱ��
			for (int i = 0; i < antNum; i++) {
				ants[i].init(etc, alpha, beta);// �����������
				//1.1Ѱ��
				for (int j = 1; j < taskNum; j++) {
					ants[i].selectNextTask(pheromone);
				}
				//1.2�Ƚϣ�ȡ����
				if (ants[i].getTimecostTotle() < bestCost) {
					bestCost = ants[i].getTimecostTotle();
					seqExcute = ants[i].getSeqExcute();
				}
				
				/*//1.3��һֻ���������߹���·�����ͷ���Ϣ��
				float delta=Q / ants[i].getTimecostTotle();
				for(MCT mct:ants[i].getSeqExcute()){
					ants[i].getDelta()[mct.taskIndex][mct.vmIndex] = delta;
				}*/
			}

			// 2.��ÿֻ���ϸ�����Ϣ��
			updatePheromone();

			
			System.out.println("��"+g+"����"+bestCost);
			System.out.println(min+"  "+max);
		}

		// ��ӡ��ѽ��
		printOptimal();
	}
	private float min=Float.MAX_VALUE,max=Float.MIN_VALUE;
	// ������Ϣ��
	private void updatePheromone() {
		// ��Ϣ�ػӷ�
		for (int i = 0; i < taskNum; i++)
			for (int j = 0; j < vmNum; j++){
				pheromone[i][j] = pheromone[i][j] * (1 - rho);
				if(pheromone[i][j]<minP)
					pheromone[i][j]=minP; 
				if(pheromone[i][j]<min)
					min= pheromone[i][j];
			}
		
		//��Ӣ����
		float deltaTao=rho*Q/bestCost;
		for(MCT mct:seqExcute){
			pheromone[mct.taskIndex][mct.vmIndex] += deltaTao;
			if(pheromone[mct.taskIndex][mct.vmIndex]>maxP)
				pheromone[mct.taskIndex][mct.vmIndex]=maxP;
			if(pheromone[mct.taskIndex][mct.vmIndex]>max){
				max=pheromone[mct.taskIndex][mct.vmIndex];
			}
		}
		System.out.println(deltaTao);
		System.out.println(minP+"  "+maxP);
	}

	private void printOptimal() {
		System.out.println("The optimal length is: " + bestCost);
		System.out.println("The optimal tour is: ");
		for (int i = 0; i < taskNum; i++) {
			System.out.print(seqExcute.get(i).taskIndex+","+seqExcute.get(i).vmIndex+"->");
		}
		 
	}

	

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		ExpData expData= ExpDataTool.get("E:/Cloudsimʹ��/�������/100task-5vm.json", 1); 
		ACO aco = new ACO(expData.taskNum, expData.vmNum, expData.taskNum,100, 1.f, 5.f, 0.5f); 
		aco.init(expData);
		aco.solve();
	}

}
