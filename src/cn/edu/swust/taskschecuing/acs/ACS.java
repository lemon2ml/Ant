package cn.edu.swust.taskschecuing.acs; 

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import cn.edu.swust.taskscheduingmethed.MCT;

/**
 * 
 * ������Դ״̬
 * 
 */
public class ACS implements Cloneable {
 
	private Vector<Integer> allowedTask; // ��������������
	private float[] costVM; // ÿ��������Ļ���
	private float[][] delta; // ��Ϣ���仯����
	private Float[][] etc; // �������

	private float t0;
	private float rho;
	private float alpha;
	private float beta;
	private float q0=0.5f;
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

	public ACS() {
		taskNum = 25;
	}

	/**
	 * Constructor of Ant
	 * 
	 * @param num
	 *            ��������
	 */
	public ACS(int num, int vmNum) {
		taskNum = num;
		this.vmNum = vmNum;
	}

	/**
	 * ��ʼ�����ϣ����ѡ����ʼλ��
	 * 
	 * @param etc   �������
	 * @param a   alpha
	 * @param b   beta
	 */
	public void init(Float[][] etc, float a, float b,float t0,float rho) {
		alpha = a;// ��Ϣ����Ҫ��
		beta = b; // �ɼ�����Ҫ��
		this.t0=t0;
		this.rho=rho;
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
	 * @param pheromone     ��Ϣ�ؾ���
	 */
	public void selectNextTask(float[][] pheromone) {
		MCT mct=null;
		/**α�������ѡ��**/
		float q = ThreadLocalRandom.current().nextFloat();
		if(q<=q0){
			mct = nextByQ(pheromone);
		}else{			
			mct = nextByP(pheromone);
		}
		// ������ѡ���������ȥ��selectTask
		allowedTask.remove(Integer.valueOf(mct.taskIndex));  
		//���������״̬
		costVM[mct.vmIndex] += etc[mct.taskIndex][mct.vmIndex];
		//ִ���б����
		seqExcute.add(mct);
		
		//�ֲ���Ϣ�ظ���
		pheromone[mct.taskIndex][mct.vmIndex]=(1-rho)*pheromone[mct.taskIndex][mct.vmIndex]+rho*t0;
		
		/*System.out.println(mct.taskIndex+","+mct.vmIndex);
		for (int i = 0; i < taskNum; i++){
			for (int j = 0; j < vmNum; j++){
				System.out.print(pheromone[i][j]+" ");
			}
			System.out.println();
		}*/
	}
	/**
	 * q<=q0,�������Ϣ��
	 * @param pheromone
	 * @return
	 */
	private MCT nextByQ(float[][] pheromone) {
		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;
		int selectTask=0,selectVm=0;
		for (Integer j : allowedTask) {
			for (int k = 0; k < vmNum; k++) {
				float max2 = (float) (Math.pow(pheromone[j][k], alpha) * Math.pow(
						1.0 / (etc[j][k] + costVM[k]), beta));
				if(max2>max){
					max=max2;
					selectTask=j;
					selectVm=k;
				}
				if(min<max2){
					min=max2;
				}
			}
		}
		if(min==max){
			MCT mct=nextByP(pheromone);
			return mct;
		}
		MCT mct=new MCT(selectTask, selectVm, etc[selectTask][selectVm]);
		return mct;
	}
	/**
	 * ͨ�����ʼ��㣬�����̶�ѡ�õ���һ�ڵ�
	 * @param pheromone
	 * @return
	 */
	private MCT nextByP(float[][] pheromone) {
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
		MCT mct=new MCT(selectTask, selectVm, etc[selectTask][selectVm]);
		return mct;
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
	public Vector<MCT> getSeqExcute() {
		return seqExcute;
	}
	public void setSeqExcute(Vector<MCT> seqExcute) {
		this.seqExcute = seqExcute;
	}


}