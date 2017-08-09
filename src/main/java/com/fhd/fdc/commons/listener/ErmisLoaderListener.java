/**
 *
 */
package com.fhd.fdc.commons.listener;

import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.RSACoder;
import com.fhd.dao.sys.i18n.I18nDAO;
import com.fhd.entity.sys.i18n.I18n;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 系统加载类
 *
 * @author vincent
 */
public class ErmisLoaderListener extends ContextLoaderListener {

    private Log log = LogFactory.getLog(ErmisLoaderListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);

        ApplicationContext cxt = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());

        // 检查许可证
//        verifyLicense(event);
        log.info("许可证验证成功！");


        // 加载国际化
        String json = convert2Json("zh", cxt);
        event.getServletContext().setAttribute("locale", json);
        log.info("国际化加载成功！");

        // 加载数据字典
        /*AnnotationSessionFactoryBean sessionFactory = cxt.getBean(AnnotationSessionFactoryBean.class);
		
		Session session = sessionFactory.getObject().openSession();
		Criteria criteria = session.createCriteria(DictEntry.class);
    	criteria.setCacheable(true);
		List<DictEntry> list = criteria.list();
    	for(DictEntry dict : list) {
    	   dict.getName();
    	   dict.getId();
    	}
    	session.close();
    	log.info("第一次加载数据字典");
    	
		session = sessionFactory.getObject().openSession();
		criteria = session.createCriteria(DictEntry.class);
    	criteria.setCacheable(true);
		list = criteria.list();
		for(DictEntry dict : list) {
    	   dict.getName();
    	   dict.getId();
    	}

		log.info("第二次加载数据字典");*/

    }

    /**
     * 许可证检查
     *
     * @param event
     * @author vincent
     */
    private void verifyLicense(ServletContextEvent event) {

        // 公钥
        String publicKey = "";
        // 解密后的明文
        String decrypt = "";

        try {
            Map<String, Object> keyMap = RSACoder.genKeyPair("0");
            publicKey = RSACoder.getPublicKey(keyMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File licenseFile = new File(event.getServletContext().getRealPath("/") + "/license.lic");
            // 解密
            decrypt = new String(RSACoder.decryptByPublicKey(FileUtils.readFileToByteArray(licenseFile), publicKey));
            if (StringUtils.isEmpty(decrypt)) {
                throw new RuntimeException("许可证信息不正确");
            }
        } catch (Exception e) {
            throw new RuntimeException("许可证文件未找到或信息不正确", e);
        }
        // 检查license
        String[] decrypts = StringUtils.split(decrypt, ",");

        if (!"forever".equals(decrypts[1])) {
            Date today = new Date();
            Date startDate = DateUtils.parse(decrypts[0]);
            Date endDate = DateUtils.parse(decrypts[1]);
            if (today.before(startDate)) {
                throw new RuntimeException("许可证已过期");
            }

            if (today.after(endDate)) {
                throw new RuntimeException("许可证已过期");
            }
        }


    }


    /**
     * 查询i18n实体
     *
     * @param session DB-Session
     * @return I18n
     * @author 金鹏祥
     * @since fhd　Ver 1.1
     */
    @SuppressWarnings("unchecked")
    private List<I18n> findI18nAll(Session session) {
        try {
            Criteria c = session.createCriteria(I18n.class);
            List<I18n> list = null;

            list = c.list();
            if (list.size() > 0) {
                return list;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return null;
    }


    private String convert2Json(String locale, ApplicationContext cxt) {
        int i = 0;
        String value = "";
        List<I18n> i18nList = null;
        I18nDAO o_planDAO = cxt.getBean(I18nDAO.class);//任务功能数据服务
        Session session = o_planDAO.getSessionFactory().openSession();//HIBERNATE-SESSION服务
        i18nList = this.findI18nAll(session);
        StringBuilder sb = new StringBuilder();

        sb.append("GajaxLocale={\n");
        sb.append("    'LOCALE':'").append(locale).append("',\n");
        for (int l = i18nList.size(); i < l; ++i) {
            String key = i18nList.get(i).getObjectKey();
            if (locale.equals("zh")) {
                value = i18nList.get(i).getObjectCn();
            } else if (locale.equals("en")) {
                value = i18nList.get(i).getObjectEn();
            }

            String suffix = (i == l - 1) ? "};" : ",";

            if ((null == value) || ("".equals(value.trim()))
                    || ("null".equalsIgnoreCase(value.trim())))
                sb.append("    '").append(key).append("'").append(":")
                        .append("null").append(suffix).append("\n");
            else
                sb.append("    '").append(key).append("'").append(":")
                        .append("'").append(value).append("'").append(suffix)
                        .append("\n");

        }

        return sb.toString();
    }

}

