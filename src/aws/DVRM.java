package aws;

import java.util.Scanner;

public class DVRM 
{
    public static void main(String[] args) throws Exception
    {
        MyAWSKit aws = new MyAWSKit();

        Scanner menu = new Scanner(System.in);
        Scanner id_string = new Scanner(System.in);
        int number = 0;


        while(true)
        {

            System.out.println(" ");
            System.out.println(" ");
            System.out.println("------------------------------------------------------------");
            System.out.println(" Amazon AWS Control Panel using SDK ");
            System.out.println(" ");
            System.out.println(" Cloud Computing, Computer Science Department ");
            System.out.println(" at Chungbuk National University ");
            System.out.println(" Doek Gyu Kang 2017068005");
            System.out.println("------------------------------------------------------------");
            System.out.println(" 1. list instance 2. available zones ");
            System.out.println(" 3. start instance 4. available regions ");
            System.out.println(" 5. stop instance 6. create instance ");
            System.out.println(" 7. reboot instance 8. list images ");
            System.out.println(" 99. quit ");
            System.out.println("------------------------------------------------------------");

            System.out.print("Enter an integer: ");
            number = menu.nextInt();
            switch(number)
            {
                case 1:
                    aws.listInstances();
                    break;
                case 2:
                    aws.availableZones();
                    break;
                case 3:
                    System.out.print("Enter instance id: ");
                    aws.startInstance(id_string.nextLine());
                    break;
                case 4:
                    aws.availableRegions();
                    break;
                case 5:
                    System.out.print("Enter instance id: ");
                    aws.stoptInstance(id_string.nextLine());
                    break;
                case 6:
                    System.out.print("Enter ami id: ");
                    aws.createInstance(id_string.nextLine());
                    break;
                case 7:
                    System.out.print("Enter instance id: ");
                    aws.rebootInstance(id_string.nextLine());
                    break;
                case 8:
                    aws.listImages();
                    break;
                case 99:
                    menu.close();
                    id_string.close();
                    return;
                default:
                    System.out.println("\n잘못된 입력입니다.");
                    break;
            }
            
        }
    }
}

