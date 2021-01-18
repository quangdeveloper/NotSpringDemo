package vn.vnpay.notspringdemo.util;

public interface Constant {

    interface RESPONSE {

        String RS = "RS";
        String PO_CODE = "code";
        String PO_MSG = "message";

        interface CODE {
            String OK = "200";
            String C400 = "400";
            String C403 = "403";
            String C404 = "404";
            String C405 = "405";
            String C405_PARAMETER = "405";
            String C409 = "409";
            String C999 = "999";
        }


        interface MESSAGE {

            String OK = "Thực hiện thành công";
            String C400 = "Bad gateway";
            String C403 = "Không có quyền thực hiện";
            String C404 = "Không tìm thấy dữ liệu";
            String DATA_NOT_EXISTS = "Dữ liệu không tồn tại";
            String C999 = "Lỗi hệ thống";
        }

    }
}
