package cn.edu.swust.taskscheduing.asMac;

import java.io.IOException;
import java.util.Vector;

import cn.edu.swust.taskschecuing.ant.Ant;
import cn.edu.swust.taskscheduing.ExpData;
import cn.edu.swust.taskscheduingmethed.MCT;

 
/**
 * ������Դ״̬
 * ������Ⱥ�㷨
 * �����ʾһȺ
 */
public class AC {
	
	private Ant[] ants; // ����
	private int antNum; // ��������
	private int taskNum; // ��������
	private int vmNum; //��������� 
	private float[][] pheromone; // ��Ϣ�ؾ���
	private Float[][] etc; // �������
	private float bestCost; // ��ѳ���
	Vector<MCT> seqExcute; // ���·��
	
	// ��������
	private float alpha;
	private float beta;
	private float rho;
	private float t0;
	private float Q=1.0f;
	public AC() {

	}

	/**
	 * 
	 * @param taskNum
	 * @param vmNum
	 * @param antNum
	 * @param MAX_GEN
	 * @param alf
	 * @param beta
	 * @param rho
	 */
	public AC(int taskNum,int vmNum, int antNum, float alf, float beta, float rho,float t0) {
		this.taskNum = taskNum;
		this.vmNum=vmNum;
		this.antNum = antNum;
		this.ants = new Ant[antNum];
		this.alpha = alf;
		this.beta = beta;
		this.rho = rho;
		this.t0=t0;
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
	public void init(ExpData expData){ 

		etc = expData.etc;

		// ��ʼ����Ϣ�ؾ���
		pheromone = new float[taskNum][vmNum];
		for (int i = 0; i < taskNum; i++) {
			for (int j = 0; j < vmNum; j++) {
				pheromone[i][j] = t0; // ��ʼ��Ϊ��Ϣ��
			}
		}
		bestCost = Float.MAX_VALUE;
		
		for (int i = 0; i < antNum; i++) {
			ants[i] = new Ant(taskNum,vmNum); 
		}
	}

	public void nextGen() {
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
				
				//1.3��һֻ���������߹���·�����ͷ���Ϣ��
				float delta=Q / ants[i].getTimecostTotle();
				for(MCT mct:ants[i].getSeqExcute()){
					ants[i].getDelta()[mct.taskIndex][mct.vmIndex] = delta;
				}
			}

			// 2.��ÿֻ���ϸ�����Ϣ�أ��ֲ����£�
			updatePheromone();
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
		//��Ӣ����
		float deltaTao=Q/bestCost;
		for(MCT mct:seqExcute){
			int i=mct.taskIndex;
			int j=mct.vmIndex;
			//�������Ž��������
			for(int k =0;k<ants.length;k++){
				if(ants[k].getDelta()[i][j]>0)
				pheromone[i][j] += deltaTao;	
			}
		} 
	}

	private void printOptimal() {
		System.out.println("The optimal length is: " + bestCost);
		System.out.println("The optimal tour is: ");
		for (int i = 0; i < taskNum; i++) {
			System.out.print(seqExcute.get(i).taskIndex+","+seqExcute.get(i).vmIndex+"->");
		}
		System.out.println();
	}

	public float[][] getPheromone() {
		return pheromone;
	}

	public void setPheromone(float[][] pheromone) {
		this.pheromone = pheromone;
	}
	public float getBestCost(){
		return bestCost;
	}
	


}
