
class EnumDemo {

    public static void main(String[] args) {
        PriorityEnum priorityEnum1 = PriorityEnum.HIGH;

        switch (priorityEnum1) {
            case HIGH ->
                System.out.println("High priority");
            case MEDIUM ->
                System.out.println("Medium priority");
            case LOW ->
                System.out.println("Low priority");
        }

        // PriorityEnum priorityEnum2 = PriorityEnum.valueOf("high");
        /**
         * High priority Exception in thread "main"
         * java.lang.IllegalArgumentException: No enum constant
         * PriorityEnum.high at java.base/java.lang.Enum.valueOf(Enum.java:293)
         * at PriorityEnum.valueOf(PriorityEnum.java:2) at
         * EnumDemo.main(EnumDemo.java:15)
         *
         *
         */
        // Enum comparison
        System.out.println("Priority.HIGH == Priority.MEDIUM: " + (priorityEnum1 == PriorityEnum.MEDIUM)); // false

        System.out.println("Priority.HIGH == Priority.MEDIUM: " + (priorityEnum1 == PriorityEnum.HIGH)); // true

        // Enum Ordinal()
        /**
         * ordinal() là phương thức final có sẵn trong lớp java.lang.Enum, trả
         * về vị trí khai báo của hằng enum (bắt đầu từ 0). Thứ tự này do
         * compiler tự gán theo thứ tự bạn viết trong file nguồn.
         *
         */
        System.out.println("Priority.HIGH.ordinal(): " + PriorityEnum.HIGH.ordinal());
        System.out.println("Priority.MEDIUM.ordinal(): " + PriorityEnum.MEDIUM.ordinal());

        // Enum Interation
        /**
         * values() là phương thức static do compiler tự sinh cho mỗi enum
         * (không có trong java.lang.Enum). Nó trả về một mảng mới chứa tất cả
         * các hằng enum theo đúng thứ tự khai báo.
         *
         */
        PriorityEnum[] values = PriorityEnum.values();
        for (PriorityEnum priorityEnum3 : values) {
            System.out.println(priorityEnum3);
        }

        // Enum fields and methods
        System.out.println("Month.JANUARY.getDaysAmount(): " + MonthEnum.JANUARY.getDaysAmount());
    }
}
