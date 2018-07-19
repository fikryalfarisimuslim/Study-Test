package com.sunway.averychoke.studywifidirect3.controller.connection;

import com.sunway.averychoke.studywifidirect3.manager.BaseManager;
import com.sunway.averychoke.studywifidirect3.manager.TeacherManager;
import com.sunway.averychoke.studywifidirect3.model.Mahasiswa;
import com.sunway.averychoke.studywifidirect3.model.Meeting;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by AveryChoke on 8/4/2017.
 */

public class TeacherThread implements Runnable {
    public enum Request {
        QUIZZES, QUIZ, STUDY_MATERIALS, STUDY_MATERIAL, NIMNAMA;
    }

    private static final int BUFFER_SIZE = 4 * 1024;

    private final ServerSocket mSocket;
    private TeacherManager sManager;
    private ExecutorService mExecutor;

    public TeacherThread() throws IOException {
        mSocket = new ServerSocket(BaseManager.APP_PORT_NUMBER);
        sManager = TeacherManager.getInstance();
    }

    @Override
    public void run() {
        try {
            mExecutor = Executors.newCachedThreadPool();
            while (true) {
                try {
                    // wait for student to connect
                    final Socket socket = mSocket.accept();

                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                                Request requestCode = (Request) ois.readObject();

                                switch (requestCode) {
                                    case QUIZZES:
                                        sendMeetingz(socket);
                                        break;
                                    case QUIZ:
                                        sendMeeting(socket, ois);
                                        break;
                                    case STUDY_MATERIALS:
                                        sendStudyMaterialsName(socket);
                                        break;
                                    case STUDY_MATERIAL:
                                        sendStudyMaterial(socket, ois);
                                        break;
                                    case NIMNAMA:
                                        System.out.print("AKU DISINI");

                                        String meetingName = (String) ois.readObject();
                                        String NIM = (String) ois.readObject();
                                        String Name = (String) ois.readObject();

                                        Meeting meeting = sManager.findMeeting(meetingName);
                                        Mahasiswa mahasiswa = new Mahasiswa();
                                        mahasiswa.getId();
                                        mahasiswa.setMahasiswa(NIM);
                                        mahasiswa.setCorrectAnswer(Name);
                                        meeting.getMahasiswa().add(mahasiswa);
                                        boolean success = sManager.updateMeeting(meeting, meeting.getName());
                                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                        oos.writeObject(ClassMaterialsRequestTask.Result.NIMNAMA);
                                        oos.writeObject(success);
                                        oos.flush();
                                        //ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                        //oos.writeObject(ClassMaterialsRequestTask.Result.NIMNAMA);
                                       // oos.flush();
                                        break;
                                }
                                /*
                                DataInputStream dIn = new DataInputStream(socket.getInputStream());

                                boolean done = false;
                                while(!done) {
                                    byte messageType = dIn.readByte();

                                    switch(messageType)
                                    {
                                        case 1: // Type A
                                            System.out.println("Message A: " + dIn.readUTF());
                                            break;
                                        default:
                                            done = true;
                                    }
                                }

                                dIn.close();*/
                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                                if (!socket.isClosed()) {
                                    try {
                                        socket.close();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } finally {
            disconnect();
            if (mExecutor != null && !mExecutor.isShutdown()) {
                mExecutor.shutdown();
            }
        }
    }

    // region request methods
    private void sendMeetingz(Socket socket) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(ClassMaterialsRequestTask.Result.QUIZZES);
        oos.writeObject(sManager.getVisibleMeetingz());
        oos.flush();
    }

    private void sendMeeting(Socket socket, ObjectInputStream ois) throws IOException, ClassNotFoundException {
        String quizName = (String) ois.readObject();

        // might send null
        Meeting meeting = sManager.findMeeting(quizName);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(ClassMaterialsRequestTask.Result.QUIZ);
        oos.writeObject(meeting);
        oos.flush();
    }

    private void sendStudyMaterialsName(Socket socket) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(ClassMaterialsRequestTask.Result.STUDY_MATERIALS);
        oos.writeObject(sManager.getVisibleStudyMaterialsName());
        oos.flush();
    }

    private void sendStudyMaterial(Socket socket, ObjectInputStream ois) throws IOException, ClassNotFoundException {
        String studyMaterialName = (String) ois.readObject();
        StudyMaterial studyMaterial = sManager.findStudyMaterial(studyMaterialName);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

        if (studyMaterial != null) {
            oos.writeObject(ClassMaterialsRequestTask.Result.STUDY_MATERIAL);
            long fileSize = studyMaterial.getFile().length();
            oos.writeLong(fileSize);

            // send file
            BufferedInputStream bis = null;
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                bis = new BufferedInputStream(new FileInputStream(studyMaterial.getFile()));
                int len = 0;
                while ((len = bis.read(buffer)) > 0) {
                    oos.write(buffer, 0, len);
                    oos.flush();
                }
            } finally {
                if (bis != null) {
                    bis.close();
                }
                oos.close();
            }

        } else {
            oos.writeObject(ClassMaterialsRequestTask.Result.ERROR);
        }
        oos.flush();
    }
    // endregion

    public void disconnect() {
        if (mSocket != null && !mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
