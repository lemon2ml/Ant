package cn.edu.swust.taskschecuing.antori;

import java.io.IOException;
import java.util.Vector;

import cn.edu.swust.taskscheduing.ExpData;
import cn.edu.swust.taskscheduing.ExpDataTool;
import cn.edu.swust.taskscheduingmethed.MCT;

/**
 * 
 * @author BIAO YU
 * 
 * 
 */
public class ACOORI {

	private AntORI[] ants; // ����
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

	public ACOORI() {

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
	public ACOORI(int taskn,int vmNum, int antn, int MAX_GEN, float a, float b, float r) {
		this.taskNum = taskn;
		this.vmNum=vmNum;
		this.antNum = antn;
		this.ants = new AntORI[antNum];
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
		
		// ��ȡ����
	//	ExpData expData= ExpDataTool.get("E:/works/CloudSim/Ant/bin/cn/edu/cn/swust/taskscheduing/130907140826.json", 9);
		

		etc = expData.etc;

		// ��ʼ����Ϣ�ؾ���
		pheromone = new float[taskNum][vmNum];
		for (int i = 0; i < taskNum; i++) {
			for (int j = 0; j < vmNum; j++) {
				pheromone[i][j] = 0.1f; // ��ʼ��Ϊ0.1
			}
		}
		bestCost = Integer.MAX_VALUE;
		// �����������
		for (int i = 0; i < antNum; i++) {
			ants[i] = new AntORI(taskNum,vmNum);
			ants[i].init(etc, alpha, beta);
		}
	}

	public void solve() {

		for (int g = 0; g < MAX_GEN; g++) {
			for (int i = 0; i < antNum; i++) {
				for (int j = 1; j < taskNum; j++) {
					ants[i].selectNextTask(pheromone);
				}
				if (ants[i].getTimecostTotle() < bestCost) {
					bestCost = ants[i].getTimecostTotle();
					seqExcute = ants[i].getSeqExcute();
				}
				for (int j = 0; j < taskNum; j++) {
					for(int k=0;k<vmNum;k++){
						ants[i].getDelta()[j][k] = (float) (1. / ants[i].getTimecostTotle());
					} 
				}
			}

			// ������Ϣ��
			updatePheromone();

			// ���³�ʼ������
			for (int i = 0; i < antNum; i++) {
				ants[i].init(etc, alpha, beta);
			}
			System.out.println("��"+g+"����"+bestCost);
		}

		// ��ӡ��ѽ��
		printOptimal();
	}

	// ������Ϣ��
	private void updatePheromone() {
		// ��Ϣ�ػӷ�
		for (int i = 0; i < taskNum; i++)
			for (int j = 0; j < vmNum; j++)
				pheromone[i][j] = pheromone[i][j] * (1 - rho);
		// ��Ϣ�ظ���
		for (int i = 0; i < taskNum; i++) {
			for (int j = 0; j < vmNum; j++) {
				for (int k = 0; k < antNum; k++) {
					pheromone[i][j] += ants[k].getDelta()[i][j];
				}
			}
		}
	}

	private void printOptimal() {
		System.out.println("The optimal length is: " + bestCost);
		System.out.println("The optimal tour is: ");
		for (int i = 0; i < taskNum; i++) {
			System.out.println(seqExcute.get(i).taskIndex+"--"+seqExcute.get(i).vmIndex);
		}
	}

	public AntORI[] getAnts() {
		return ants;
	}

	public void setAnts(AntORI[] ants) {
		this.ants = ants;
	}

	public int getAntNum() {
		return antNum;
	}

	public void setAntNum(int m) {
		this.antNum = m;
	}

	public int getCityNum() {
		return taskNum;
	}

	public void setCityNum(int cityNum) {
		this.taskNum = cityNum;
	}

	public int getMAX_GEN() {
		return MAX_GEN;
	}

	public void setMAX_GEN(int mAX_GEN) {
		MAX_GEN = mAX_GEN;
	}

	public float[][] getPheromone() {
		return pheromone;
	}

	public void setPheromone(float[][] pheromone) {
		this.pheromone = pheromone;
	}

	

	public void setBestLength(int bestLength) {
		this.bestCost = bestLength;
	}

	

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getBeta() {
		return beta;
	}

	public void setBeta(float beta) {
		this.beta = beta;
	}

	public float getRho() {
		return rho;
	}

	public void setRho(float rho) {
		this.rho = rho;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		ACOORI aco = new ACOORI(25, 5, 25,500, 1.f, 5.f, 0.5f); 
		ExpData expData= ExpDataTool.get("E:/Cloudsimʹ��/�������/25task-5vm.json", 9); 
		aco.init(expData);
		aco.solve();
	}

}
