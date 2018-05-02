package test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import cn.edu.swust.taskscheduingmethed.MCT;


public class test {
	public static void main(String[] args) { 
		/*String[] str={"ma","mb","key","abc"};
		MyCom com=new MyCom();
		Arrays.sort(str);
		for(String s:str){
			System.out.print(s+" ");
		}
		int a = Arrays.binarySearch(str, "key");
		System.out.println(a);*/
		/*int i=0,j=0;
		a:for( i=0;i<5;i++)
			for( j=0;j<5;j++){
				if (i==3&&j==3)break a;
			}
		 System.out.println(i+" "+j);*/
		
		
		/**
	 for (int i = 0; i < taskNum; i++) {
       boolean flag = false;
       for (Integer j:allowedTask) {    	   
         if (i == j.intValue()) {
        	 for(int k=0;k<vmNum;k++){
        		 p[i][k] = (float) (Math.pow(pheromone[i][k], alpha)*Math.pow(1.0/(etc[i][k]+costVM[k]), beta))/sum;                 
      	   } 
        	 flag = true;
             break;
         }
       }
       if (flag == false) {
    	   for(int k=0;k<vmNum;k++){
      		 p[i][k] = 0; 
    	   } 
       }
     }
		 */
	/*	Integer[] allowedTask = new Integer[]{1,3,4};
		float[][] t= new float[5][5];
		
		 for (Integer j:allowedTask) { 
	        	 for(int k=0;k<5;k++){
	        		 t[j][k] = 0.1f;                 
	      	   } 
	       }
		
		for(int i=0;i<5;i++){
			for(int j=0;j<5;j++){
				System.out.print(t[i][j] +" ");
			}
			System.out.println();
		}*/
			
		 /*Vector<Integer> allowedTask=new Vector<Integer>();
		 allowedTask.add(0);
		 allowedTask.add(1);
		 allowedTask.add(2);
		 allowedTask.add(3);
		 allowedTask.add(4);  
		 
		 int v[][]=new int[][]{	{1,5,90,2,3},
		 						{2,100,1,2,3},
		 						{4,76,2,121,1},
		 						{15,80,7,3,210},
		 						{11,90,2,12,13}
		 };
		
		
		 	while(!allowedTask.isEmpty()){
		 		 float p[][]= new float[5][5];
		 		 int sum=0;
				 for(Integer i : allowedTask)
					 for(int j=0;j<5;j++){
						 sum+=v[i][j];
					 }
				 for(Integer i : allowedTask)
					 for(int j=0;j<5;j++){
						 p[i][j]=v[i][j]/(float)sum;
					 }
		 		
		 		float sleectP = ThreadLocalRandom.current().nextFloat();
				Integer selectTask = 0, selectVm = 0; 
				float sum1 = 0.f;
				a: for (Integer i : allowedTask) {
					for (int j = 0; j < 5; j++) {
						sum1 += p[i][j];
						if (sum1 >= sleectP) {
							selectTask = i;
							selectVm = j;
							break a;
						}
					}
				}
				allowedTask.remove(selectTask);
				System.out.println(selectTask+"-"+selectVm+"  ");
				System.out.println("p..."+sleectP);
				for(int i=0;i<5;i++){
					 for(int j=0;j<5;j++){
						System.out.print(p[i][j] +"  ");
					 }
					 System.out.println();
				}
		 	}*/
		 	
		MCT mct=null;
		/**Î±Ëæ»ú±ÈÀýÑ¡Ôñ**/
		int count=0;
		for(int i=100;i>=0;i--){
			float q = ThreadLocalRandom.current().nextFloat();
			if(q<=0.2)count++;
			System.out.println(q +"---"+(q<=0.2));
		}
		System.out.println(count);
	} 
}

class MyCom implements Comparator<String>{

	@Override
	public int compare(String str1, String str2) { 
		return str1.compareTo(str2);
	}

	
}
