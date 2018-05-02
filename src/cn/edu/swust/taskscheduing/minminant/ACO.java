package cn.edu.swust.taskscheduing.minminant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import cn.edu.swust.taskscheduing.ExpData;
import cn.edu.swust.taskscheduing.ExpDataTool;
import cn.edu.swust.taskscheduingmethed.MCT;

/**
 * 
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
	private  float bestCost; // ��ѳ���
	Vector<MCT> seqExcute; // ���·��
	
	private float Q=5.0f;
	// ��������
	private float alpha;
	private float beta;
	private float rho;

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

	public synchronized void setSeqExcute(Vector<MCT> seqExcute) {
		this.seqExcute = seqExcute;
	}
	public synchronized void updateBest(float bestCost,Vector<MCT> seqExcute){
		if (bestCost < this.bestCost) {
			this.bestCost=bestCost;
			this.seqExcute=seqExcute;
		}
	}
	/**
	 * ��ʼ��ACO�㷨��
	 * @param filename �����ļ��������ļ��洢���г��нڵ���������
	 * @throws IOException
	 */
	public void init(ExpData expData) throws IOException {
		
		etc = expData.etc;

		// ��ʼ����Ϣ�ؾ���
		pheromone = initPheromone(expData);
		
		bestCost = Integer.MAX_VALUE;
		// �����������
		for (int i = 0; i < antNum; i++) {
			ants[i] = new Ant(taskNum,vmNum);
			ants[i].init(etc, alpha, beta);
		}
	}
	private ACOschedue acoSchedue;
	public void solve(ACOschedue acoSchedue) {
		this.acoSchedue=acoSchedue;
		solove1();
		/*for (int g = 0; g < MAX_GEN; g++) {
			for (int i = 0; i < antNum; i++) {
				//Ѱ��
				for (int j = 1; j < taskNum; j++) {
					ants[i].selectNextTask(pheromone);
				}
				//���ҳ�����·���Ƿ�Ϊ����
				if (ants[i].getTimecostTotle() < bestCost) {
					bestCost = ants[i].getTimecostTotle();
					seqExcute = ants[i].getSeqExcute();
				}
				//��Ϣ���ͷ�
				for (int j = 0; j < taskNum; j++) {
					for(int k=0;k<vmNum;k++){
						ants[i].getDelta()[j][k] = (float) (Q / ants[i].getTimecostTotle());
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
		printOptimal();*/
	}
	public void solove1(){
		for (int i = 0; i < antNum; i++) {
			final Ant ant =ants[i];
			new Thread(
					new Runnable() { 
						@Override
						public void run() { 
							//Ѱ��
							for (int j = 1; j < taskNum; j++) {
								ant.selectNextTask(pheromone);
							}
							//���ҳ�����·���Ƿ�Ϊ����
							updateBest(ant.getTimecostTotle(), ant.getSeqExcute());
							gonextGEN();
							//��Ϣ���ͷ�
							for (int j = 0; j < taskNum; j++) {
								for(int k=0;k<vmNum;k++){
									ant.getDelta()[j][k] = (float) (Q / ant.getTimecostTotle());
								} 
							} 
						}
					}
					).start();
			
		}
	}
	private  int antC=0;
	private int genC=0;
	public synchronized void gonextGEN(){
		antC++;
		if(antC==antNum){			
			System.out.println("��"+genC+"����"+bestCost);
			if(genC<MAX_GEN){
				genC++;
				antC=0;
				// ������Ϣ��
				updatePheromone();
				// ���³�ʼ������
				for (int i = 0; i < antNum; i++) {
					ants[i].init(etc, alpha, beta);
				}
				solove1();
			}else{
				printOptimal();
				long t2=System.currentTimeMillis();
				System.out.println("compute Time:"+(t2-t1));
			}
			
		}
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
			System.out.print (seqExcute.get(i).taskIndex+"-"+seqExcute.get(i).vmIndex+"--->");
		}
		acoSchedue.toSimulation(seqExcute);
	}

	public Ant[] getAnts() {
		return ants;
	}
	public void setAnts(Ant[] ants) {
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
	public synchronized void setBestCost(float bestLength) {
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
	//��Ϣ�ؾ���	
	private float[][] initPheromone(ExpData expData){
		seqExcute = new Vector<MCT>();
		float[][] pheromone = new float[taskNum][vmNum];
		
		for (int i = 0; i < taskNum; i++) {
			for (int j = 0; j < vmNum; j++) {
				pheromone[i][j] = 0.1f; // ��ʼ��Ϊ0.1
			}
		}
		int cloudletsCont = expData.taskNum; 
    	float[] costVM = new float[expData.vmNum];

    	List<Boolean> cloudlets2 = new ArrayList<Boolean>();
    	for (int i = 0; i < taskNum; i++){
    		cloudlets2.add(false);
    	}
	//	List vmList=null;
    	while(cloudletsCont>0){     		
    		float minCostVm = Float.MAX_VALUE;
			int minCostVmIndex =0;
			for(int k = 0; k<costVM.length;k++){
				if(costVM[k]<minCostVm){
					minCostVm = costVM[k];
					minCostVmIndex= k;
				}
			} 
    		
    		float min=Float.MAX_VALUE;
    		int taskIndex=0;
    		for(int i=0;i<taskNum;i++){
    			if(cloudlets2.get(i)==null)continue;
    			if(expData.etc[i][minCostVmIndex]<min){
    				min =expData.etc[i][minCostVmIndex];
    				taskIndex=i;
    			} 
        	} 
    		costVM[minCostVmIndex]+=min;
    		cloudlets2.set(taskIndex, null);
    		cloudletsCont--;
    		seqExcute.add(new MCT(taskIndex,minCostVmIndex,min));
    	}
    	bestCost=costVM[0];
    	for(int i=0;i<costVM.length;i++){
    		if(bestCost<costVM[i]){
    			bestCost=costVM[i];
    		}
    	}
    	for(MCT mct:seqExcute){
    		pheromone[mct.taskIndex][mct.vmIndex]+=Q/bestCost;
    	}
    	return pheromone;
	}
	/**
	 * @param args
	 * @throws IOException
	 */
	static long t1=System.currentTimeMillis();
	public static void main(String[] args) throws Exception { 
		ACO aco = new ACO(100, 5, 100,500, 1.f, 5.f, 0.5f); 
		ExpData expData= ExpDataTool.get("C:/Users/WQF/Desktop/ʵ������/100task-5vm.json", 8); 
		aco.init(expData);
	//	aco.solve1();
		
	}

}
