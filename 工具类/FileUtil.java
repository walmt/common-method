package com.gzpsc.util;

import com.gzpsc.bean.FileMsg;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * @author gmr
 *
 */
public class FileUtil {

	/**
	 * 获取由对象得来的路径
	 * 
	 * @param o
	 *            Objec对象
	 * @return
	 */
	public static String getHashPath(Object o) {
		String hex = Integer.toHexString(o.hashCode());
		return "/" + hex.charAt(0) + "/" + hex.charAt(1);
	}

	/**
	 * 后缀判断
	 * @param fileName 文件名
	 * @param isExcept 是否排除策略（不能是指定后缀） 
	 * @param suffixes 后缀(需要加.)
	 * @return
	 */
	public static boolean isInSuffix(String fileName,boolean isExcept,String...suffixes){
		for(String suffix:suffixes){
			if(fileName.endsWith(suffix)){
				return !isExcept;
			}
		}
		return isExcept;
	}
	
	/**
	 * 后缀判断(判断是否为指定后缀)
	 * @param fileName 文件名
	 * @param suffixes 后缀(需要加.)
	 * @return
	 */
	public static boolean isInSuffix(String fileName,String...suffixes){
		return isInSuffix(fileName, false, suffixes);
	}
	/**
	 * 保存文件
	 * 
	 * @param files
	 *            文件集
	 * @param savePath
	 *            保存路径
	 * @return
	 * @throws IOException
	 */
	public static List<FileMsg> saveFiles(MultipartFile[] files, String savePath) {
		return saveFiles(files, savePath, null);
	}

	/**
	 * 保存文件集(有默认后缀)
	 * 
	 * @param files
	 *            文件集
	 * @param savePath
	 *            保存路径
	 * @param defaultSuffix
	 *            默认后缀
	 * @return
	 * @throws IOException
	 */
	public static List<FileMsg> saveFiles(MultipartFile[] files,
			String savePath, String defaultSuffix) {
		List<FileMsg> list = new ArrayList<FileMsg>();
		try {
			for (MultipartFile file : files) {
				list.add(saveFile(file, savePath, defaultSuffix));
			}
		} catch (Exception e) {
			for (FileMsg fileMsg : list) {
				deleteFile(fileMsg.getSavePath(), fileMsg.getSaveName());
			}
			throw new RuntimeException(e);
		}
		return list;
	}

	public static FileMsg saveFile(MultipartFile file, String savePath)
			throws IOException {
		return saveFile(file, savePath, null);
	}

	public static FileMsg saveFile(File file, String savePath) {
		return saveFile(file, savePath, null);
	}

	/**
	 * 保存文件(有默认后缀)
	 * 
	 * @param file
	 *            文件
	 * @param savePath
	 *            保存路径
	 * @param defaultSuffix
	 *            默认后缀
	 * @return
	 * @throws IOException
	 */
	public static FileMsg saveFile(MultipartFile file, String savePath,
			String defaultSuffix) throws IOException {
		String fileName = null;// 文件名
		String suffix = defaultSuffix;// 后缀
		String saveName = null;
		File dirFile = null;// 目录链
		File destFile = null;// 最终目录(目录链+保存名)

		fileName = file.getOriginalFilename();// 得到文件名

		/*-----------------------------------
		 * 处理绝对路径问题
		 * 处理例如C:/xx/xx.xx
		 * 只截取最后的xx.xx
		 */
		int lastIndex1 = fileName.lastIndexOf("\\");// 获取最后一个"\"的位置
		int lastIndex2 = fileName.lastIndexOf("/");// 获取最后一个"/"的位置
		if (lastIndex2 > lastIndex1) {
			lastIndex1 = lastIndex2;
		}
		if (lastIndex1 != -1) {
			// 假如是完整路径，截取最后一小段
			fileName = fileName.substring(lastIndex1 + 1);// 获取文件名称
		}
		if (suffix == null) {
			/*
			 * 判定是从文件处提取后缀
			 */
			suffix = fileName.substring(fileName.lastIndexOf('.') + 1);
		}
		saveName = file.hashCode() + "."+suffix;// 获取保存名称，路径+后缀(后缀先忽略)

		dirFile = new File(savePath);// 得到目录链
		dirFile.mkdirs();// 创建目录链
		destFile = new File(dirFile, saveName);// 生成最终目录(包含文件名)

		// 保存文件进目录当中
		// 将文件输入到destFile目录中
		System.out.println(destFile.getAbsolutePath());
		FileUtils.copyInputStreamToFile(file.getInputStream(), destFile);

		FileMsg fm = new FileMsg(savePath, fileName, saveName);
		return fm;
	}

	public static FileMsg saveFile(File file, String savePath,
			String defaultSuffix) {
		String fileName = null;// 文件名
		String suffix = defaultSuffix;// 后缀
		String saveName = null;
		File dirFile = null;// 目录链
		File destFile = null;// 最终目录(目录链+保存名)
		InputStream in = null;

		fileName = file.getName();// 得到文件名

		/*-----------------------------------
		 * 处理绝对路径问题
		 * 处理例如C:/xx/xx.xx
		 * 只截取最后的xx.xx
		 */
		int lastIndex1 = fileName.lastIndexOf("\\");// 获取最后一个"\"的位置
		int lastIndex2 = fileName.lastIndexOf("/");// 获取最后一个"/"的位置
		if (lastIndex2 > lastIndex1) {
			lastIndex1 = lastIndex2;
		}
		if (lastIndex1 != -1) {
			// 假如是完整路径，截取最后一小段
			fileName = fileName.substring(lastIndex1 + 1);// 获取文件名称
		}
		if (suffix == null) {
			/*
			 * 判定是从文件处提取后缀
			 */
			suffix = fileName.substring(fileName.lastIndexOf('.') + 1);
		}
		saveName = file.hashCode() + "." + suffix;// 获取保存名称，用户名+后缀

		dirFile = new File(savePath);// 得到目录链
		dirFile.mkdirs();// 创建目录链
		destFile = new File(dirFile, saveName);// 生成最终目录(包含文件名)

		// 保存文件进目录当中
		try {
			// 将文件输入到destFile目录中
			in = new FileInputStream(file);
			FileUtils.copyInputStreamToFile(in, destFile);
		} catch (IOException e) {
			// throw new MsgException("服务器保存失败");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		FileMsg fm = new FileMsg(savePath, fileName, saveName);
		return fm;
	}

	/**
	 * 把文件保存成压缩包
	 * 
	 * @param files
	 * @throws IOException
	 * @return 成功返回true,失败返回false
	 */
	public static boolean saveFilesToZip(MultipartFile[] files, String path,
			String zipName) {
		ZipOutputStream zOut = null;
		File f = new File(path);
		f.mkdirs();
		try {
			zOut = new ZipOutputStream(new FileOutputStream(path + "/"
					+ zipName));
			int len = -1;
			byte[] b = new byte[1024];
			for (MultipartFile file : files) {
				zOut.putNextEntry(new ZipEntry(file.getOriginalFilename()));
				InputStream in = file.getInputStream();
				while ((len = in.read(b)) != -1) {
					zOut.write(b, 0, len);
				}
			}
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			if (zOut != null) {
				try {
					zOut.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 添加文件到已经有的zip文件里面
	 * 
	 * @param files
	 * @param path
	 * @param zipName
	 * @return
	 */
	public static boolean addFilesToZip(MultipartFile[] files, String path,
			String zipName) {
		ZipOutputStream zOut = null;
		ZipFile zipFile = null;
		File ft = new File(path + "/" + zipName + "t");// 临时文件对象
		File fo = new File(path + "/" + zipName);// 已有文件对象
		try {
			// 得到输出到临时文件流
			zOut = new ZipOutputStream(new FileOutputStream(ft));
			// 生成目录链
			new File(path).mkdirs();
			// 若存在原zip文件，则先将原zip文件输出到临时文件当中
			try {
				zipFile = new ZipFile(fo);
				Enumeration<? extends ZipEntry> e = zipFile.entries();
				while (e.hasMoreElements()) {
					ZipEntry zipEntry = e.nextElement();
					zOut.putNextEntry(zipEntry);
					IOUtils.copy(zipFile.getInputStream(zipEntry), zOut);
				}
			} catch (IOException e1) {
			} finally {
				if (zipFile != null) {
					try {
						zipFile.close();
					} catch (IOException e) {
					}
				}
			}

			// 将上传文件压缩
			for (MultipartFile file : files) {
				int i = 0;
				String preName = null;// 文件名
				String suffix = null;// 后缀
				while (true) {
					/*
					 * 判断文件名是否重复，文件名重复加后缀五次，如果还重复，不再保存该文件
					 */
					try {
						if (i == 0) {
							zOut.putNextEntry(new ZipEntry(file
									.getOriginalFilename()));
						} else if (i > 5) {
						} else {
							if (preName == null) {
								preName = file.getOriginalFilename().substring(
										file.getOriginalFilename().lastIndexOf(
												".") + 1);
							}
							if (suffix == null) {

							}
							zOut.putNextEntry(new ZipEntry(file
									.getOriginalFilename() + i));
						}
						break;
					} catch (Exception e) {
						i++;
					}
				}
				InputStream in = file.getInputStream();
				IOUtils.copy(in, zOut);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (zOut != null) {
				try {
					zOut.close();
				} catch (IOException e) {
				}
			}
		}
		// 删除原有文件
		if (zipFile != null) {
			fo.delete();
		}
		fo.delete();
		// 临时文件改名成原文件
		ft.renameTo(fo);
		return true;
	}

	/**
	 * 获取压缩文件里的文件名
	 * 
	 * @param path
	 * @param zipName
	 * @return
	 */
	public static List<String> getFileNames(String path, String zipName) {
		List<String> list = new ArrayList<String>();
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(path + "/" + zipName);
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			while (e.hasMoreElements()) {
				list.add(e.nextElement().getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	/**
	 * 删除压缩包里的文件
	 * 
	 * @param path
	 * @param fileNames
	 */
	public static boolean deleteFiles(String path, String zipName,
			String[] fileNames) {
		ZipOutputStream zOut = null;
		ZipFile zipFile = null;
		File ft = new File(path + "/" + zipName + "t");// 临时文件对象
		File fo = new File(path + "/" + zipName);// 已有文件对象
		try {
			// 得到输出到临时文件流
			zOut = new ZipOutputStream(new FileOutputStream(ft));
			// 生成目录链
			new File(path).mkdirs();
			// 若存在原zip文件，则先将原zip文件输出到临时文件当中
			try {
				zipFile = new ZipFile(fo);
				Enumeration<? extends ZipEntry> e = zipFile.entries();
				while (e.hasMoreElements()) {
					ZipEntry zipEntry = e.nextElement();
					boolean tag = false;
					for (String fileName : fileNames) {
						if (zipEntry.getName().equals(fileName)) {
							tag = true;
							break;
						}
					}
					if (tag) {
						continue;
					}
					zOut.putNextEntry(zipEntry);
					IOUtils.copy(zipFile.getInputStream(zipEntry), zOut);
				}
			} catch (IOException e1) {
			} finally {
				if (zipFile != null) {
					try {
						zipFile.close();
					} catch (IOException e) {
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (zOut != null) {
				try {
					zOut.close();
				} catch (IOException e) {
				}
			}
		}
		// 删除原有文件
		if (zipFile != null) {
			fo.delete();
		}
		fo.delete();
		// 临时文件改名成原文件
		ft.renameTo(fo);
		return true;
	}

	/**
	 * 删除压缩包里的文件
	 * 
	 * @param path
	 * @param fileName
	 */
	public static boolean deleteFile(String path, String zipName,
			String fileName) {

		String[] fileNames = new String[] { fileName };
		return deleteFiles(path, zipName, fileNames);
	}

	public static void deleteFile(String path, String fileName) {
		File file = new File(path + "/" + fileName);
		file.delete();
	}
}
