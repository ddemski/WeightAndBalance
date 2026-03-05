import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {

    // ---------- FIXED VALUES ----------

    static final double WEIGHT_ME = 190;

    // Fuel
    static final double USABLE_FUEL_LBS = 318;  // 53 gallons * 6
    static final double TAXI_FUEL = 8.4;        // always subtracted

    // Arms
    static final double ARM_FRONT = 37;
    static final double ARM_REAR = 73;
    static final double ARM_BAGGAGE = 95;
    static final double ARM_FUEL = 48;


    public static void main(String[] args) {
        double maxAllowableWeight;
        double maxEnvelopeTop;
        Scanner sc = new Scanner(System.in);

        // ---------- PROMPT FOR AIRCRAFT DATA ----------
        System.out.print("Enter aircraft model (R/S or K-N): ");
        String model = sc.next().trim().toUpperCase();

        boolean isKN = false;

        if (model.equals("R/S") || model.equals("RS") || model.equals("R") || model.equals("S")) {
            maxAllowableWeight = 2550;
            maxEnvelopeTop = 2550;
            isKN = false;
            System.out.println("Model recognized: 172R/S (Max Weight 2550 lbs)");
        } else if (model.equals("K-N") || model.equals("KLMN") || model.equals("K") || model.equals("N") || model.equals("L") || model.equals("M")) {
            maxAllowableWeight = 2300;
            maxEnvelopeTop = 2300;
            isKN = true;
            System.out.println("Model recognized: 172K–N (Max Weight 2300 lbs)");
        } else {
            maxAllowableWeight = 2550;
            maxEnvelopeTop = 2550;
            isKN = false;
            System.out.println("Unknown model — defaulting to 172R/S");
        }


        System.out.print("Enter Basic Empty Weight: ");
        double EMPTY_WEIGHT = sc.nextDouble();

        System.out.print("Enter Basic Empty Moment: ");
        double EMPTY_MOMENT = sc.nextDouble();

        System.out.print("Enter Front Left Pilot's Weight: ");
        double WEIGHT_STUDENT = sc.nextDouble();

        System.out.print("Enter Front Right Pilot's Weight: ");
        double WEIGHT_INSTRUCTOR = sc.nextDouble();

        System.out.print("Enter Rear Passenger Weight: ");
        double WEIGHT_REAR = sc.nextDouble();

        System.out.print("Enter Baggage Weight (max 120 lbs): ");
        double WEIGHT_BAGGAGE = sc.nextDouble();

        System.out.print("How many gallons of fuel will you burn? ");
        double fuelBurnGallons = sc.nextDouble();
        double FUEL_BURN = fuelBurnGallons * 6;


        // ---- Compute Empty Weight Arm (Moment ÷ Weight) ----
        double EMPTY_ARM = Math.round((EMPTY_MOMENT / EMPTY_WEIGHT) * 100.0) / 100.0;

        // ---------- FIXED LOADS ----------
        double frontWeight = WEIGHT_STUDENT + WEIGHT_INSTRUCTOR;
        double rearWeight = WEIGHT_REAR;
        double baggageWeight = WEIGHT_BAGGAGE;
        double fuelWeight = USABLE_FUEL_LBS;

        // Moments
        double mEmpty = EMPTY_MOMENT;
        double mFront = frontWeight * ARM_FRONT;
        double mRear = rearWeight * ARM_REAR;
        double mBag = baggageWeight * ARM_BAGGAGE;
        double mFuel = fuelWeight * ARM_FUEL;
        double mRamp = mFuel + mEmpty + mFront + mRear + mBag;

        // Zero Fuel
        double zeroFuelWeight = EMPTY_WEIGHT + frontWeight + rearWeight + baggageWeight;
        double zeroFuelMoment = mEmpty + mFront + mRear + mBag;
        double zeroFuelArm = Math.round((zeroFuelMoment / zeroFuelWeight) * 100.0) / 100.0;

        // Takeoff (subtract taxi fuel)
        double takeoffWeight = zeroFuelWeight + fuelWeight - TAXI_FUEL;
        double takeoffMoment = zeroFuelMoment + mFuel - (TAXI_FUEL * ARM_FUEL);
        double takeoffCG = Math.round((takeoffMoment / takeoffWeight) * 100.0) / 100.0;

        // Ramp Weight
        double rampWeight = zeroFuelWeight + fuelWeight;

        // Landing (subtract burn)
        double landingWeight = takeoffWeight - FUEL_BURN;
        double landingMoment = takeoffMoment - (FUEL_BURN * ARM_FUEL);
        double landingCG = Math.round((landingMoment / landingWeight) * 100.0) / 100.0;

        // ---------- OUTPUT ----------
        System.out.println("\n================= WEIGHT & BALANCE MATRIX =================");

        System.out.printf("%-22s | %10s | %10s | %12s%n", "ITEM", "WEIGHT(lbs)", "ARM(in.)", "MOMENT");
        System.out.println("---------------------------------------------------------------");

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n", "Basic Empty Weight", EMPTY_WEIGHT, EMPTY_ARM, mEmpty);

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n", "Front Pilots", frontWeight, ARM_FRONT, mFront);

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n", "Rear Passengers", rearWeight, ARM_REAR, mRear);

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n", "Baggage 120 lbs max", baggageWeight, ARM_BAGGAGE, mBag);

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n", "Zero Fuel Weight", zeroFuelWeight, zeroFuelArm, zeroFuelMoment);

        System.out.printf("%-22s | %10.2f | %10s | %12.2f%n", "Usable Fuel", fuelWeight, "48", mFuel);

        System.out.printf("%-22s | %10.2f | %10s | %12.2f%n", "Ramp Weight", rampWeight, "-----", mRamp);

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n", "Taxi Fuel", -TAXI_FUEL, ARM_FUEL, TAXI_FUEL * ARM_FUEL * -1);

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n", "Takeoff Weight", takeoffWeight, takeoffCG, takeoffMoment);

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n", "Fuel Burn", -FUEL_BURN, ARM_FUEL, FUEL_BURN * ARM_FUEL * -1);

        System.out.printf("%-22s | %10.2f | %10.2f | %12.2f%n", "Landing Weight", landingWeight, landingCG, landingMoment);

        System.out.println("---------------------------------------------------------------");

        System.out.printf("ZFW CG: %.2f   TOW CG: %.2f   LW CG: %.2f%n", zeroFuelArm, takeoffCG, landingCG);

        sc.close();


        String envelopeCode;

        if (isKN) {
            envelopeCode = """
                    # ---- C172 K–N Envelope ----
                    # Forward sloping limit
                    plt.plot([35, 38.5], [1950, 2300], color='black', linewidth=2)
                    # Forward vertical limit
                    plt.vlines(x=35, ymin=1500, ymax = 1950, color='black', linewidth=2)
                    # Aft vertical limit
                    plt.vlines(x=47.3, ymin=1500, ymax=2300, color='black', linewidth=2)
                    
                    # Top horizontal
                    plt.hlines(y=2300, xmin=38.5, xmax=47.3, color='black', linewidth=2)
                    
                    # Bottom floor
                    plt.hlines(y=1500, xmin=35, xmax=47.3, color='black', linewidth=2)
                    """;
        } else {
            envelopeCode = """
                    # ---- C172 R/S Envelope ----
                    plt.vlines(x=35, ymin=1500, ymax=1950, color='black', linewidth=2)
                    plt.vlines(x=40.5, ymin=1500, ymax=2200, color='black', linewidth=2)
                    plt.vlines(x=47.5, ymin=1500, ymax=2550, color='black', linewidth=2)
                    
                    plt.hlines(y=2200, xmin=37.5, xmax=40.5, color='black', linewidth=2)
                    plt.hlines(y=2550, xmin=41, xmax=47.5, color='black', linewidth=2)
                    
                    x = [35, 41, 47.5]
                    y = [1950, 2550, 2550]
                    plt.plot(x,y, color='black', linewidth=2)
                    """;
        }


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
                
                %s
                
                plt.xlabel("CG Location (inches)")
                plt.ylabel("Weight (lbs)")
                plt.title("Weight vs CG")
                plt.xlim(34,48)
                plt.ylim(1500, %f)
                
                plt.grid(True)
                plt.savefig("C:/Users/Domenic/IdeaProjects/WeightAndBalance/wb_plot.png", dpi=150, bbox_inches='tight')
                plt.show()
                """.formatted(zeroFuelWeight, takeoffWeight, landingWeight, zeroFuelArm, takeoffCG, landingCG, envelopeCode, maxEnvelopeTop + 100);


        System.out.println("Creating File 'wb_plot.png'...");


        try (java.io.FileWriter writer = new java.io.FileWriter("plot.py")) {
            writer.write(plotScript);
        } catch (Exception e) {
            System.out.println("Error writing Python script: " + e.getMessage());
        }

        try {
            Process p = Runtime.getRuntime().exec("python plot.py"); // or "python3" if that works
            System.out.print("File Created: wb_plot.png");
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

