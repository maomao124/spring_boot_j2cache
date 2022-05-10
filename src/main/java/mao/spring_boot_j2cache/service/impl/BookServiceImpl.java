package mao.spring_boot_j2cache.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import mao.spring_boot_j2cache.dao.BookDao;
import mao.spring_boot_j2cache.entity.Book;
import mao.spring_boot_j2cache.service.IBookService;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Project name(项目名称)：spring_boot_vue_and_element_UI_book_information_management_system
 * Package(包名): mao.book_management.service.impl
 * Class(类名): IBookServiceImpl
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/5/9
 * Time(创建时间)： 13:13
 * Version(版本): 1.0
 * Description(描述)： BookService的实现类
 */


@Service
public class BookServiceImpl extends ServiceImpl<BookDao, Book> implements IBookService
{

    @Autowired
    private BookDao bookDao;

    @Override
    public boolean saveBook(Book book)
    {
        return bookDao.insert(book) > 0;
    }

    @Override
    public boolean modify(Book book)
    {
        return bookDao.updateById(book) > 0;
    }

    @Override
    public boolean delete(Integer id)
    {
        return bookDao.deleteById(id) > 0;
    }

    @Override
    public IPage<Book> getPage(int currentPage, int pageSize)
    {
        IPage<Book> page = new Page<>(currentPage, pageSize);
        bookDao.selectPage(page, null);
        return page;
    }

    @Override
    public IPage<Book> getPage(int currentPage, int pageSize, Book book)
    {
        LambdaQueryWrapper<Book> bookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        bookLambdaQueryWrapper.like(Strings.isNotEmpty(book.getType()), Book::getType, book.getType());
        bookLambdaQueryWrapper.like(Strings.isNotEmpty(book.getName()), Book::getName, book.getName());
        bookLambdaQueryWrapper.like(Strings.isNotEmpty(book.getDescription()), Book::getDescription, book.getDescription());
        IPage<Book> page = new Page<>(currentPage, pageSize);
        bookDao.selectPage(page, bookLambdaQueryWrapper);
        return page;
    }

    @Autowired
    private CacheChannel cache;

    @Override
    public Book getBookById(Integer id)
    {
         Book book = (Book)cache.get("book" , String.valueOf(id)).getValue();

        if (book != null)
        {
            return book;
        }
        Book book1 = bookDao.selectById(id);
        if (book1 != null)
        {
            cache.set("book" , String.valueOf(id), book1);
        }
        return book1;
    }
}
