import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {

    // ---------- FIXED VALUES ----------
    static final double WEIGHT_ME = 180;
    static final double WEIGHT_INSTRUCTOR = 250;
    static final double WEIGHT_REAR = 20;
    static final double WEIGHT_BAGGAGE = 10;

    // Fuel
    static final double USABLE_FUEL_LBS = 318;  // 53 gallons * 6
    static final double TAXI_FUEL = 8.4;        // always subtracted
    static final double FUEL_BURN = 132;        // always subtracted

    // Arms
    static final double ARM_FRONT = 37;
    static final double ARM_REAR = 73;
    static final double ARM_BAGGAGE = 95;
    static final double ARM_FUEL = 48;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // ---------- PROMPT FOR AIRCRAFT DATA ----------
        System.out.print("Enter Basic Empty Weight: ");
        double EMPTY_WEIGHT = sc.nextDouble();

        System.out.print("Enter Basic Empty Moment: ");
        double EMPTY_MOMENT = sc.nextDouble();

        // ---- Compute Empty Weight Arm (Moment ÷ Weight) ----
        double EMPTY_ARM = Math.round((EMPTY_MOMENT / EMPTY_WEIGHT) * 100.0) / 100.0;

        // ---------- FIXED LOADS ----------
        double frontWeight = WEIGHT_ME + WEIGHT_INSTRUCTOR;
        double rearWeight = WEIGHT_REAR;
        double baggageWeight = WEIGHT_BAGGAGE;
        double fuelWeight = USABLE_FUEL_LBS;

        // Moments
        double mEmpty = EMPTY_MOMENT;
        double mFront = frontWeight * ARM_FRONT;
        double mRear = rearWeight * ARM_REAR;
        double mBag = baggageWeight * ARM_BAGGAGE;
        double mFuel = fuelWeight * ARM_FUEL;

        // Zero Fuel
        double zeroFuelWeight = EMPTY_WEIGHT + frontWeight + rearWeight + baggageWeight;
        double zeroFuelMoment = mEmpty + mFront + mRear + mBag;
        double zeroFuelArm = Math.round((zeroFuelMoment / zeroFuelWeight) * 100.0) / 100.0;

        // Takeoff (subtract taxi fuel)
        double takeoffWeight = zeroFuelWeight + fuelWeight - TAXI_FUEL;
        double takeoffMoment = zeroFuelMoment + mFuel - (TAXI_FUEL * ARM_FUEL);
        double takeoffCG = Math.round((takeoffMoment / takeoffWeight) * 100.0) / 100.0;

        // Landing (subtract burn)
        double landingWeight = takeoffWeight - FUEL_BURN;
        double landingMoment = takeoffMoment - (FUEL_BURN * ARM_FUEL);
        double landingCG = Math.round((landingMoment / landingWeight) * 100.0) / 100.0;

        // ---------- OUTPUT ----------
        System.out.println("\n================= WEIGHT & BALANCE MATRIX =================");

        System.out.printf("%-22s | %10s | %10s | %12s%n",
                "ITEM", "WEIGHT", "ARM", "MOMENT");
        System.out.println("---------------------------------------------------------------");

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n",
                "Basic Empty Weight", EMPTY_WEIGHT, EMPTY_ARM, mEmpty);

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n",
                "Front Pilots", frontWeight, ARM_FRONT, mFront);

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n",
                "Rear Passengers", rearWeight, ARM_REAR, mRear);

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n",
                "Baggage 120 lbs max", baggageWeight, ARM_BAGGAGE, mBag);

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n",
                "Zero Fuel Weight", zeroFuelWeight, zeroFuelArm, zeroFuelMoment);

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n",
                "Usable Fuel", fuelWeight, ARM_FUEL, mFuel);

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n",
                "Taxi Fuel", TAXI_FUEL, ARM_FUEL, TAXI_FUEL * ARM_FUEL * -1);

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n",
                "Fuel Burn", FUEL_BURN, ARM_FUEL, FUEL_BURN * ARM_FUEL * -1);

        System.out.printf("%-22s | %10.2f | %10s | %12.2f%n",
                "Takeoff Weight", takeoffWeight, "-----", takeoffMoment);

        System.out.printf("%-22s | %10.2f | %10s | %12.2f%n",
                "Landing Weight", landingWeight, "-----", landingMoment);

        System.out.println("---------------------------------------------------------------");

        System.out.printf("ZFW CG: %.2f   TOW CG: %.2f   LW CG: %.2f%n",
                zeroFuelArm, takeoffCG, landingCG);

        sc.close();

        // ---------- Python Plot ----------
        String plotScript = """
                import matplotlib.pyplot as plt
                
                weights = [%f, %f, %f]
                cgs = [%f, %f, %f]
                labels = ['Zero Fuel', 'Takeoff', 'Landing']
                
                plt.figure(figsize=(6,6))
                plt.scatter(cgs, weights)
                
                for cg, wt, lbl in zip(cgs, weights, labels):
                    plt.text(cg, wt, lbl, fontsize=10, ha='left', va='bottom')
                
                plt.vlines(x=35, ymin=1500, ymax=1950, color='black', linestyle='-', linewidth=2)
                plt.vlines(x=40.5, ymin=1500, ymax=2200, color='black', linestyle='-', linewidth=2)
                plt.vlines(x=47.5, ymin=1500, ymax=2550, color='black', linestyle='-', linewidth=2)
                
                plt.hlines(y=2200, xmin=37.5, xmax=40.5, color='black', linestyle='-', linewidth=2)
                plt.hlines(y=2550, xmin=41, xmax=47.5, color='black', linestyle='-', linewidth=2)
                
                x = [35, 41, 47.5]
                y = [1950, 2550, 2550]
                plt.plot(x,y, color='black',linewidth=2)
                
                
                plt.xlabel("CG Location (inches)")
                plt.ylabel("Weight (lbs)")
                plt.title("Weight vs CG (Auto-Generated)")
                plt.xlim(34,48)
                plt.ylim(1500,2600)
                plt.grid(True)
                plt.show()
                plt.savefig("C:/Users/Domenic/IdeaProjects/WeightAndBalance/wb_plot.png", dpi=150, bbox_inches='tight')
                """.formatted(
                zeroFuelWeight, takeoffWeight, landingWeight,
                zeroFuelArm, takeoffCG, landingCG
        );

        System.out.println("Current directory: " + System.getProperty("user.dir"));

        try (java.io.FileWriter writer = new java.io.FileWriter("plot.py")) {
            writer.write(plotScript);
        } catch (Exception e) {
            System.out.println("Error writing Python script: " + e.getMessage());
        }

        try {
            Process p = Runtime.getRuntime().exec("python plot.py"); // or "python3" if that works
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            BufferedReader errReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = errReader.readLine()) != null) {
                System.out.println("PYTHON ERROR: " + line);
            }
            p.waitFor();
            System.out.println("\nPlot generated: wb_plot.png");
        } catch (Exception e) {
            System.out.println("Error running Python: " + e.getMessage());
        }

    }
}
