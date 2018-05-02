package cn.edu.swust.taskschecuing.ant; 

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import cn.edu.swust.taskscheduingmethed.MCT;

/**
 * 
 * ������Դ״̬
 * 
 */
public class Ant implements Cloneable {
 
	private Vector<Integer> allowedTask; // ��������������
	private float[] costVM; // ÿ��������Ļ���
	private float[][] delta; // ��Ϣ���仯����
	private Float[][] etc; // �������


	private float alpha;
	private float beta;

	private Float timecostTotle; // ·������
	private Vector<MCT> seqExcute;// ·��
	private int taskNum; // ��������
	private int vmNum; // ���������

	public float[][] getDelta() {
		return delta;
	}

	public void setDelta(float[][] delta) {
		this.delta = delta;
	}

	public Ant() {
		taskNum = 25;
	}

	/**
	 * Constructor of Ant
	 * 
	 * @param num
	 *            ��������
	 */
	public Ant(int num, int vmNum) {
		taskNum = num;
		this.vmNum = vmNum;
	}

	/**
	 * ��ʼ�����ϣ����ѡ����ʼλ��
	 * 
	 * @param timeCost
	 *            �������
	 * @param a
	 *            alpha
	 * @param b
	 *            beta
	 */
	public void init(Float[][] etc, float a, float b) {
		alpha = a;// ��Ϣ����Ҫ��
		beta = b; // �ɼ�����Ҫ��
		timecostTotle = null;
		this.etc = etc; // ��ʼ��ÿ��������ÿ���������ִ�е�ʱ�仨��
		// ��ʼ��ÿ���������ǰ�Ļ���
		costVM = new float[vmNum]; 
		for (int i = 0; i < vmNum; i++) { // ��ʼ������
			costVM[i] = 0;// ��ʼ��ÿ����������ѵ�ʱ��
		}
		
		allowedTask = new Vector<Integer>();// ������ʵ������б�
		delta = new float[taskNum][vmNum]; // �ͷ���Ϣ��
		for (int i = 0; i < taskNum; i++) {
			allowedTask.add(Integer.valueOf(i));
			for (int j = 0; j < vmNum; j++) {
				delta[i][j] = 0.f;
			}
		}
		
		int taskseq = ThreadLocalRandom.current().nextInt(taskNum);// ���ѡ��һ������		
		int vmseq = ThreadLocalRandom.current().nextInt(vmNum);// ���ѡ��һ������� 		
		costVM[vmseq] += etc[taskseq][vmseq]; // �����������ʱ������		
		
		allowedTask.remove(Integer.valueOf(taskseq));// ������м�������
		//ִ�ж���
		seqExcute = new Vector<MCT>();
		seqExcute.add(new MCT(taskseq, vmseq, etc[taskseq][vmseq]));
	}

	/**
	 * ѡ����һ��<T,V>
	 * 
	 * @param pheromone
	 *            ��Ϣ�ؾ���
	 */
	public void selectNextTask(float[][] pheromone) {
		float[][] p = new float[taskNum][vmNum];
		float sum = 0.0f;
		// �����ĸ����
		for (Integer i : allowedTask) {
			for (int j = 0; j < vmNum; j++) {
				sum += Math.pow(pheromone[i][j], alpha)
						* Math.pow(1.0 / (etc[i][j] + costVM[j]), beta);
			}
		}
		// ������ʾ���
		for (Integer j : allowedTask) {
			for (int k = 0; k < vmNum; k++) {
				p[j][k] = (float) (Math.pow(pheromone[j][k], alpha) * Math.pow(
						1.0 / (etc[j][k] + costVM[k]), beta)) / sum;
			}
		}

		// ���̶�ѡ����һ�����������
		float sleectP = ThreadLocalRandom.current().nextFloat();
		Integer selectTask = 0, selectVm = 0;

		float sum1 = 0.f;
		a: for (Integer i : allowedTask) {
			for (int j = 0; j < vmNum; j++) {
				sum1 += p[i][j];
				if (sum1 >= sleectP) {
					selectTask = i;
					selectVm = j;
					break a;
				}
			}
		}

		// ������ѡ���������ȥ��selectTask
		allowedTask.remove(selectTask);  
		//���������״̬
		costVM[selectVm] += etc[selectTask][selectVm];
		//ִ���б����
		seqExcute.add(new MCT(selectTask, selectVm, etc[selectTask][selectVm]));
	}

	/**
	 * ����·������
	 * 
	 * @return ·������
	 */
	private float calculateTotalCost() {
		float cost = 0;
		for (int i = 0; i < vmNum; i++) {
			if (costVM[i] > cost) {
				cost = costVM[i];
			}
		}
		return cost;
	}
	public Float getTimecostTotle() {
		if (timecostTotle == null)
			timecostTotle = calculateTotalCost();
		return timecostTotle;
	}
	public void setTimecostTotle(Float timecostTotle) {
		this.timecostTotle = timecostTotle;
	}
	public Vector<Integer> getAllowedTask() {
		return allowedTask;
	}
	public void setAllowedTask(Vector<Integer> allowedTask) {
		this.allowedTask = allowedTask;
	}
	public float[] getCostVM() {
		return costVM;
	}
	public void setCostVM(float[] costVM) {
		this.costVM = costVM;
	}
	public Float[][] getEtc() {
		return etc;
	}
	public void setEtc(Float[][] etc) {
		this.etc = etc;
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
	public Vector<MCT> getSeqExcute() {
		return seqExcute;
	}
	public void setSeqExcute(Vector<MCT> seqExcute) {
		this.seqExcute = seqExcute;
	}
	public int getTaskNum() {
		return taskNum;
	}
	public void setTaskNum(int taskNum) {
		this.taskNum = taskNum;
	}
	public int getVmNum() {
		return vmNum;
	}
	public void setVmNum(int vmNum) {
		this.vmNum = vmNum;
	}

}