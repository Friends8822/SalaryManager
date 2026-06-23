package com.salarymanager.service;

import com.salarymanager.model.dto.Requests.*;
import com.salarymanager.model.entity.*;
import com.salarymanager.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepo;
    private final CategoryRepository categoryRepo;
    private final BudgetRepository budgetRepo;

    public ExpenseService(ExpenseRepository er, CategoryRepository cr, BudgetRepository br) {
        this.expenseRepo = er; this.categoryRepo = cr; this.budgetRepo = br;
    }

    public List<Category> getCategories(Long userId) {
        return categoryRepo.findAvailableByUser(userId);
    }

    public Category addCategory(Long userId, CategoryRequest req) {
        long count = categoryRepo.findAvailableByUser(userId).stream().filter(c -> c.getUserId() != null).count();
        if (count >= 10) throw new RuntimeException("自定义分类已达上限(10个)");
        Category c = new Category();
        c.setUserId(userId); c.setName(req.name); c.setIcon(req.icon); c.setColor(req.color);
        c.setIsPreset(0); c.setSortOrder(100);
        return categoryRepo.save(c);
    }

    @Transactional
    public void deleteCategory(Long userId, Long catId) {
        Category c = categoryRepo.findById(catId).orElseThrow(() -> new RuntimeException("分类不存在"));
        if (c.getIsPreset() == 1) throw new RuntimeException("预设分类不可删除");
        if (!c.getUserId().equals(userId)) throw new RuntimeException("无权操作");
        Category other = categoryRepo.findAvailableByUser(userId).stream()
                .filter(x -> x.getIsPreset() == 1 && "其他".equals(x.getName())).findFirst()
                .orElse(categoryRepo.findAvailableByUser(userId).get(0));
        // Move expenses to "其他"
        YearMonth ym = YearMonth.now();
        LocalDate start = ym.atDay(1), end = ym.atEndOfMonth();
        List<Expense> exps = expenseRepo.findByUserIdAndCategoryAndMonth(userId, catId, start, end);
        for (Expense e : exps) { e.setCategoryId(other.getId()); expenseRepo.save(e); }
        c.setDeletedAt(java.time.LocalDateTime.now());
        categoryRepo.save(c);
    }

    public Expense addExpense(Long userId, ExpenseRequest req) {
        Expense e = new Expense();
        e.setUserId(userId); e.setCategoryId(req.categoryId);
        e.setAmount(req.amount.setScale(2, RoundingMode.HALF_UP));
        e.setNote(req.note != null ? req.note : "");
        e.setExpenseDate(LocalDate.parse(req.expenseDate));
        return expenseRepo.save(e);
    }

    public Expense updateExpense(Long userId, Long id, ExpenseRequest req) {
        Expense e = expenseRepo.findById(id).orElseThrow(() -> new RuntimeException("记录不存在"));
        if (!e.getUserId().equals(userId)) throw new RuntimeException("无权操作");
        e.setCategoryId(req.categoryId);
        e.setAmount(req.amount.setScale(2, RoundingMode.HALF_UP));
        e.setNote(req.note != null ? req.note : "");
        e.setExpenseDate(LocalDate.parse(req.expenseDate));
        return expenseRepo.save(e);
    }

    @Transactional
    public void deleteExpense(Long userId, Long id) {
        Expense e = expenseRepo.findById(id).orElseThrow(() -> new RuntimeException("记录不存在"));
        if (!e.getUserId().equals(userId)) throw new RuntimeException("无权操作");
        e.setDeletedAt(java.time.LocalDateTime.now());
        expenseRepo.save(e);
    }

    public List<Expense> listExpenses(Long userId, String month, Long categoryId) {
        YearMonth ym = (month != null) ? YearMonth.parse(month) : YearMonth.now();
        LocalDate start = ym.atDay(1), end = ym.atEndOfMonth();
        if (categoryId != null) return expenseRepo.findByUserIdAndCategoryAndMonth(userId, categoryId, start, end);
        return expenseRepo.findByUserIdAndMonth(userId, start, end);
    }

    public Map<String, Object> getMonthlyStats(Long userId, String monthStr) {
        YearMonth ym = (monthStr != null) ? YearMonth.parse(monthStr) : YearMonth.now();
        LocalDate start = ym.atDay(1), end = ym.atEndOfMonth();
        YearMonth prevYm = ym.minusMonths(1);

        BigDecimal total = expenseRepo.sumByUserIdAndMonth(userId, start, end);
        BigDecimal prevTotal = expenseRepo.sumByUserIdAndMonth(userId, prevYm.atDay(1), prevYm.atEndOfMonth());

        List<Object[]> breakdown = expenseRepo.categoryBreakdown(userId, start, end);
        List<Map<String, Object>> cats = new ArrayList<>();
        for (Object[] row : breakdown) {
            Map<String, Object> m = new LinkedHashMap<>();
            Long catId = (Long) row[0];
            Category cat = categoryRepo.findById(catId).orElse(null);
            m.put("categoryId", catId);
            m.put("categoryName", cat != null ? cat.getName() : "未知");
            m.put("icon", cat != null ? cat.getIcon() : "📦");
            m.put("color", cat != null ? cat.getColor() : "#888");
            m.put("amount", row[1]);
            if (total.compareTo(BigDecimal.ZERO) > 0)
                m.put("percent", ((BigDecimal) row[1]).divide(total, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(1, RoundingMode.HALF_UP));
            else m.put("percent", BigDecimal.ZERO);
            cats.add(m);
        }

        List<Object[]> trend = expenseRepo.dailyTrend(userId, start, end);
        List<Map<String, Object>> daily = new ArrayList<>();
        for (Object[] row : trend) { Map<String, Object> m = new LinkedHashMap<>(); m.put("date", row[0].toString()); m.put("amount", row[1]); daily.add(m); }

        // Budget
        Optional<Budget> budgetOpt = budgetRepo.findByUserIdAndBudgetMonthAndCategoryIdIsNull(userId, ym.toString());
        Map<String, Object> budgetInfo = null;
        if (budgetOpt.isPresent()) {
            Budget b = budgetOpt.get();
            BigDecimal spent = total != null ? total : BigDecimal.ZERO;
            BigDecimal progress = b.getAmount().compareTo(BigDecimal.ZERO) > 0
                    ? spent.divide(b.getAmount(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            budgetInfo = new LinkedHashMap<>();
            budgetInfo.put("budgetAmount", b.getAmount());
            budgetInfo.put("spentAmount", spent);
            budgetInfo.put("remaining", b.getAmount().subtract(spent));
            budgetInfo.put("progressPercent", progress);
            budgetInfo.put("isOver", spent.compareTo(b.getAmount()) >= 0);
            budgetInfo.put("isWarn", !(spent.compareTo(b.getAmount()) >= 0) && progress.compareTo(new BigDecimal("80")) >= 0);
        }

        double change = 0;
        if (prevTotal != null && prevTotal.compareTo(BigDecimal.ZERO) > 0 && total != null)
            change = total.subtract(prevTotal).divide(prevTotal, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("month", ym.toString());
        result.put("totalExpense", total != null ? total : BigDecimal.ZERO);
        result.put("prevMonthExpense", prevTotal != null ? prevTotal : BigDecimal.ZERO);
        result.put("changePercent", Math.round(change * 10.0) / 10.0);
        result.put("categories", cats);
        result.put("dailyTrend", daily);
        result.put("budget", budgetInfo);
        return result;
    }

    public Budget setBudget(Long userId, BudgetRequest req) {
        Budget b;
        if (req.categoryId == null) {
            b = budgetRepo.findByUserIdAndBudgetMonthAndCategoryIdIsNull(userId, req.budgetMonth).orElse(new Budget());
        } else {
            b = budgetRepo.findByUserIdAndBudgetMonthAndCategoryId(userId, req.budgetMonth, req.categoryId).orElse(new Budget());
        }
        b.setUserId(userId); b.setBudgetMonth(req.budgetMonth); b.setCategoryId(req.categoryId); b.setAmount(req.amount);
        return budgetRepo.save(b);
    }

    public Budget getBudget(Long userId, String month) {
        return budgetRepo.findByUserIdAndBudgetMonthAndCategoryIdIsNull(userId, month).orElse(null);
    }
}
