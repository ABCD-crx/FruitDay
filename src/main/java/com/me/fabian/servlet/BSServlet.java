package com.me.fabian.servlet;

import com.me.fabian.service.FruitService;
import com.me.fabian.service.UserService;
import com.me.fabian.vo.Fruit;
import com.me.fabian.vo.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.util.List;


// 后台管理
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50   // 50MB
)

public class BSServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        req.setCharacterEncoding("utf-8");
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        req.setCharacterEncoding("utf-8");
        String key = req.getParameter("key");

        if ("alluser".equals(key)) {
            doAlluser(req, resp);
        } else if ("deluser".equals(key)) {
            doDeluser(req, resp);
        } else if ("adduser".equals(key)) {
            doAdduser(req, resp);
        } else if ("upuser".equals(key)) {
            doUpuser(req, resp);
        } else if ("finduser".equals(key)) {
            doFinduser(req, resp);
        } else if ("allfruit".equals(key)) {
            doAllfruit(req, resp);
        } else if ("addfruit".equals(key)) {
            doAddfruit(req, resp);
        } else if ("findfruit".equals(key)) {
            doFindfruit(req, resp);
        } else if ("delfruit".equals(key)) {
            doDelfruit(req, resp);
        } else if ("hotfruit".equals(key)) {
            doHotfruit(req, resp);
        } else if ("upfruit".equals(key)) {
            doUpfruit(req, resp);
        } else if ("toAdmin".equals(key)) {
            toAdmin(req, resp);
        }

    }
    protected void toAdmin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null){
            req.getRequestDispatcher("/BSindex.jsp").forward(req,resp);
        }else {
            req.getRequestDispatcher("/login.jsp").forward(req,resp);
        }
    }

    protected void doUpfruit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fname = req.getParameter("fname");
        String spec = req.getParameter("spec");
        double up = Double.parseDouble(req.getParameter("up"));
        String t1 = req.getParameter("t1");
        String t2 = req.getParameter("t2");
        int inum = Integer.parseInt(req.getParameter("inum"));
        int fid = Integer.parseInt(req.getParameter("fid"));
        Fruit fruit = new Fruit(fid, fname, spec, up, t1, t2, inum);

        FruitService.up(fruit);

        doAllfruit(req, resp);

    }

    protected void doHotfruit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Fruit> fruits = FruitService.hot();

        req.setAttribute("allfruit", fruits);

        req.getRequestDispatcher("AllFruit.jsp").forward(req, resp);
    }

    protected void doDelfruit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int fid = Integer.parseInt(req.getParameter("fid"));

        FruitService.del(fid);

        doAllfruit(req, resp);


    }

    protected void doFindfruit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int fid = Integer.parseInt(req.getParameter("fid"));
        Fruit fruit = FruitService.info(fid);

        req.setAttribute("fruit", fruit);

        req.getRequestDispatcher("UpFruit.jsp").forward(req, resp);
    }


    protected void doAddfruit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {



        String fname = req.getParameter("fname");
        String spec = req.getParameter("spec");
        double up = Double.parseDouble(req.getParameter("up"));
        String t1 = req.getParameter("t1");
        String t2 = req.getParameter("t2");
        int inum = Integer.parseInt(req.getParameter("inum"));
        int fid = Integer.parseInt(req.getParameter("fid"));
        Fruit fruit = new Fruit(fid, fname, spec, up, t1, t2, inum);

        boolean boo = FruitService.add(fruit);
        if (boo) {

            // 添加照片
            // 照片添加功能
            String savePath = "D:\\daima\\keshe\\FruitDay\\src\\main\\webapp\\img\\fruits\\"+fid+"/"; // 指定文件保存路径，根据您的需要进行更改
            System.out.println(savePath);

            File fileSaveDir = new File(savePath);
            if (!fileSaveDir.exists()) {
                fileSaveDir.mkdir(); // 如果目录不存在，创建它
                System.out.println("创建成功");
            }

            String fileName = "";
            String errorMsg = "";

            try {
                for (Part part : req.getParts()) {
                    fileName = extractFileName(part);
                    if (!fileName.isEmpty()) {
                        String modifiedFileName = "(1).jpg";
                        String filePath = savePath + File.separator + modifiedFileName;
                        part.write(filePath);
                        System.out.println(filePath+"写入成功");
                        break; // 只处理第一个文件

                    }
                }
            } catch (Exception e) {
                errorMsg = "文件上传失败: " + e.getMessage();
            }

            if (errorMsg == null || errorMsg.isEmpty()) {
                req.setAttribute("message", "文件上传成功");
            } else {
                req.setAttribute("message", errorMsg);
            }

            System.out.println(errorMsg);

            doAllfruit(req, resp);
        } else {
            req.getRequestDispatcher("AddFruit.jsp").forward(req, resp);
        }




    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }

    protected void doAllfruit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Fruit> fruits = FruitService.all();

        req.setAttribute("allfruit", fruits);

        req.getRequestDispatcher("AllFruit.jsp").forward(req, resp);

    }

    protected void doAlluser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = UserService.alluser();
//        System.out.println(users.toString());
        req.setAttribute("allusers", users);

        req.getRequestDispatcher("Allusers.jsp").forward(req, resp);


    }

    protected void doDeluser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int id = Integer.parseInt(req.getParameter("id"));

        User user = new User(id);

        UserService.del(user);


        doAlluser(req, resp);

    }

    protected void doAdduser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uname = req.getParameter("name1");
        String email = req.getParameter("email1");
        String phone = req.getParameter("phone1");
        String pwd = req.getParameter("pwd1");

        User user = new User(email, phone, pwd, uname);

        User boo = UserService.add(user);

        if (boo != null) {
            doAlluser(req, resp);
        }


    }

    protected void doUpuser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uname = req.getParameter("name2");
        String email = req.getParameter("email2");
        String phone = req.getParameter("phone2");
        String pwd = req.getParameter("pwd2");
        int id = Integer.parseInt(req.getParameter("id"));

        User user = new User(id, email, phone, pwd, uname);
        System.out.println( "UpUser=="+user.toString());
        UserService.upUser(user);

        doAlluser(req,resp);
    }

    protected void doFinduser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));

        User user = UserService.findById(id);

        if (user != null) {
            req.setAttribute("user", user);
        }

        req.getRequestDispatcher("UpUser.jsp").forward(req, resp);
    }
}
