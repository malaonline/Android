//
//  ClassScheduleViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 3/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let ClassScheduleViewCellReuseID = "ClassScheduleViewCellReuseID"
private let ClassScheduleViewHeaderReuseID = "ClassScheduleViewHeaderReuseID"
private let kCalendarUnitYMD: NSCalendarUnit = [.Year, .Month, .Day]

public class ClassScheduleViewController: PDTSimpleCalendarViewController, PDTSimpleCalendarViewDelegate, ClassScheduleViewCellDelegate {


    // MARK: - Components
    /// 保存按钮
    private lazy var saveButton: UIButton = {
        let saveButton = UIButton(
            title: "今天",
            titleColor: MalaColor_82B4D9_0,
            target: self,
            action: "scrollToToday"
        )
        saveButton.setTitleColor(MalaColor_E0E0E0_95, forState: .Disabled)
        return saveButton
    }()
    
    // MARK: - Life Cycle
    override public func viewDidLoad() {
        super.viewDidLoad()

        configure()
        loadStudentCourseTable()
    }

    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    

    // MARK: - Private Method
    private func configure() {
        // Calendar
        delegate = self
        weekdayHeaderEnabled = true
        
        // rightBarButtonItem
        let spacerRight = UIBarButtonItem(barButtonSystemItem: .FixedSpace, target: nil, action: nil)
        spacerRight.width = -MalaLayout_Margin_5
        let rightBarButtonItem = UIBarButtonItem(customView: saveButton)
        navigationItem.rightBarButtonItems = [rightBarButtonItem, spacerRight]
        
        // register
        collectionView?.registerClass(ClassScheduleViewCell.self, forCellWithReuseIdentifier: ClassScheduleViewCellReuseID)
        collectionView?.registerClass(ClassScheduleViewHeader.self, forSupplementaryViewOfKind: UICollectionElementKindSectionHeader, withReuseIdentifier: ClassScheduleViewHeaderReuseID)
    }
    
    private func loadStudentCourseTable() {
        ///  获取学生可用时间表
        getStudentCourseTable(failureHandler: { (reason, errorMessage) -> Void in
            
            }, completion: { (courseList) -> Void in
                
        })
    }
    
    
    // MARK: - DataSource
    override public func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(ClassScheduleViewCellReuseID, forIndexPath: indexPath) as! ClassScheduleViewCell
        cell.delegate = self
        
        let firstOfMonth = self.firstOfMonthForSection(indexPath.section)
        let cellDate = self.dateForCellAtIndexPath(indexPath)
        
        let cellDateComponents = self.calendar.components(kCalendarUnitYMD, fromDate: cellDate)
        let firstOfMonthsComponents = self.calendar.components(kCalendarUnitYMD, fromDate: firstOfMonth)

        var isToday = false
        var isSelected = false
        var isCustomDate: Bool? = false
        
        if cellDateComponents.month == firstOfMonthsComponents.month {
            isSelected = self.isSelectedDate(cellDate) && (indexPath.section == self.sectionForDate(cellDate))
            isToday = self.isTodayDate(cellDate)
            cell.setDate(date: cellDate, calendar: self.calendar)
            isCustomDate = self.delegate?.simpleCalendarViewController?(self, shouldUseCustomColorsForDate: cellDate)
        }else {
            cell.setDate(date: nil, calendar: nil)
        }
        
        if isToday {
            cell.isToday = isToday
        }

        if isSelected {
            cell.selected = isSelected
        }
        
        if self.isEnabledDate(cellDate) || (isCustomDate == true) {
            cell.refreshCellColors()
        }
        
        cell.layer.shouldRasterize = true
        cell.layer.rasterizationScale = UIScreen.mainScreen().scale

        return cell
    }
    
    
    // MARK: - ClassScheduleViewCell Delegate
    public func classScheduleViewCell(cell: ClassScheduleViewCell, shouldUseCustomColorsForDate date: NSDate) -> Bool {
        if self.isEnabledDate(date) {
            return true
        }
        
        return (self.delegate?.simpleCalendarViewController?(self, shouldUseCustomColorsForDate: date) ?? false)
    }
    
    public func classScheduleViewCell(cell: ClassScheduleViewCell, circleColorForDate date: NSDate) -> UIColor? {
        if self.isEnabledDate(date) {
            return cell.circleDefaultColor
        }
        
        return (self.delegate?.simpleCalendarViewController?(self, circleColorForDate: date) ?? nil)
    }
    
    public func classScheduleViewCell(cell: ClassScheduleViewCell, textColorForDate date: NSDate) -> UIColor? {
        if self.isEnabledDate(date) {
            return cell.textDisabledColor
        }
        
        return (self.delegate?.simpleCalendarViewController?(self, textColorForDate: date) ?? nil)
    }
    
    
    // MARK: - Event Response
    @objc private func scrollToToday() {
        scrollToDate(NSDate(), animated: true)
    }
}